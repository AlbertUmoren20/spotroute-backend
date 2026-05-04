package com.spotroute.service;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.PaymentStatus;
import com.spotroute.dto.response.PaymentResponse;
import com.spotroute.entity.Booking;
import com.spotroute.entity.DriverProfile;
import com.spotroute.exception.BadRequestException;
import com.spotroute.exception.ResourceNotFoundException;
import com.spotroute.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final WalletService walletService;

    @Value("${flutterwave.secret-key}")
    private String flutterwaveSecretKey;

    @Value("${flutterwave.base-url}")
    private String flutterwaveBaseUrl;

    private final WebClient.Builder webClientBuilder;

    @Transactional
    public PaymentResponse initializePayment(String userEmail, String bookingId, String redirectUrl) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Booking not found");
        }

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Booking is already paid");
        }

        String reference = "SPR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        booking.setPaymentReference(reference);
        bookingRepository.save(booking);

        // Build Flutterwave payment request
        Map<String, Object> payload = Map.of(
            "tx_ref", reference,
            "amount", booking.getTotalAmount(),
            "currency", "NGN",
            "redirect_url", redirectUrl != null ? redirectUrl : "http://localhost:5000/payment/callback",
            "customer", Map.of(
                "email", booking.getUser().getEmail(),
                "name", booking.getUser().getName(),
                "phonenumber", booking.getUser().getPhone()
            ),
            "meta", Map.of("bookingId", booking.getId()),
            "customizations", Map.of(
                "title", "SpotRoute Payment",
                "description", "Payment for " + booking.getRide().getRoute().getOrigin()
                             + " → " + booking.getRide().getRoute().getDestination()
            )
        );

        try {
            WebClient client = webClientBuilder.baseUrl(flutterwaveBaseUrl).build();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.post()
                    .uri("/payments")
                    .header("Authorization", "Bearer " + flutterwaveSecretKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && "success".equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                String paymentLink = (String) data.get("link");

                return PaymentResponse.builder()
                        .paymentReference(reference)
                        .paymentLink(paymentLink)
                        .status("pending")
                        .message("Payment initialized successfully")
                        .build();
            }

            throw new BadRequestException("Failed to initialize payment with Flutterwave");

        } catch (Exception e) {
            log.error("Flutterwave init error: {}", e.getMessage());
            // Return reference so frontend can still handle manually if needed
            return PaymentResponse.builder()
                    .paymentReference(reference)
                    .status("error")
                    .message("Payment gateway error: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public PaymentResponse verifyPayment(String transactionId, String paymentReference) {
        Booking booking = bookingRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for reference: " + paymentReference));

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            return PaymentResponse.builder()
                    .paymentReference(paymentReference)
                    .status("success")
                    .message("Already verified")
                    .build();
        }

        try {
            WebClient client = webClientBuilder.baseUrl(flutterwaveBaseUrl).build();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.get()
                    .uri("/transactions/" + transactionId + "/verify")
                    .header("Authorization", "Bearer " + flutterwaveSecretKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && "success".equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                String txStatus = (String) data.get("status");
                double paidAmount = ((Number) data.get("amount")).doubleValue();

                if ("successful".equals(txStatus)
                        && booking.getTotalAmount().compareTo(BigDecimal.valueOf(paidAmount)) == 0) {

                    booking.setPaymentStatus(PaymentStatus.PAID);
                    booking.setStatus(BookingStatus.CONFIRMED);
                    booking.setFlutterwaveTransactionId(transactionId);
                    bookingRepository.save(booking);

                    // Credit the driver wallet
                    DriverProfile driver = booking.getRide().getDriver();
                    walletService.creditDriver(
                        driver,
                        booking.getTotalAmount(),
                        "Booking payment for ride " + booking.getRide().getId(),
                        paymentReference
                    );

                    return PaymentResponse.builder()
                            .paymentReference(paymentReference)
                            .status("success")
                            .message("Payment verified successfully")
                            .build();
                }
            }

            // Payment failed — release the reserved seats
            booking.setPaymentStatus(PaymentStatus.FAILED);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            Booking savedBooking = booking;
            savedBooking.getRide().setBookedSeats(
                    savedBooking.getRide().getBookedSeats() - savedBooking.getSeatCount()
            );

            return PaymentResponse.builder()
                    .paymentReference(paymentReference)
                    .status("failed")
                    .message("Payment verification failed")
                    .build();

        } catch (Exception e) {
            log.error("Flutterwave verify error: {}", e.getMessage());
            return PaymentResponse.builder()
                    .paymentReference(paymentReference)
                    .status("error")
                    .message("Verification error: " + e.getMessage())
                    .build();
        }
    }
}
