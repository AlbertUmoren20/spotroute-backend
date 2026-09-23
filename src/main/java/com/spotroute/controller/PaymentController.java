//package com.spotroute.controller;
//
//import com.spotroute.dto.request.PaymentInitRequest;
//import com.spotroute.dto.request.PaymentVerifyRequest;
//import com.spotroute.dto.response.ApiResponse;
//import com.spotroute.dto.response.PaymentResponse;
//import com.spotroute.service.PaymentService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/payments/v1")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//
//    @PostMapping("/initialize")
//    public ResponseEntity<ApiResponse<PaymentResponse>> initialize(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @Valid @RequestBody PaymentInitRequest req) {
//        PaymentResponse response = paymentService.initializePayment(
//                userDetails.getUsername(),
//                req.getBookingId(),
//                req.getRedirectUrl()
//        );
//        return ResponseEntity.ok(ApiResponse.ok("Payment initialized", response));
//    }
//
//    @PostMapping("/verify")
//    public ResponseEntity<ApiResponse<PaymentResponse>> verify(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @Valid @RequestBody PaymentVerifyRequest req) {
//        PaymentResponse response = paymentService.verifyPayment(
//                req.getTransactionId(),
//                req.getPaymentReference()
//        );
//        return ResponseEntity.ok(ApiResponse.ok(response));
//    }
//
//    /**
//     * Flutterwave webhook — no auth token, validates via signature header.
//     * Flutterwave sends POST with JSON body + "verif-hash" header.
//     */
//    @PostMapping("/webhook")
//    public ResponseEntity<Void> webhook(
//            @RequestHeader(value = "verif-hash", required = false) String verifHash,
//            @RequestBody String payload) {
//        // In production: validate verifHash against your Flutterwave webhook secret
//        // For now we log and return 200 so Flutterwave doesn't retry
//        return ResponseEntity.ok().build();
//    }
//}
