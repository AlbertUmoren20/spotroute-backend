package com.spotroute.dto.response;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.PaymentStatus;
import com.spotroute.entity.Booking;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String id;
    private String userId;
    private RideResponse ride;
    private int seatCount;
    private String pickupPoint;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String paymentReference;
    private LocalDateTime createdAt;

    public static BookingResponse from(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .ride(RideResponse.from(booking.getRide()))
                .seatCount(booking.getSeatCount())
                .pickupPoint(booking.getPickupPoint())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .paymentReference(booking.getPaymentReference())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
