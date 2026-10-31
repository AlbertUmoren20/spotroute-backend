package com.spotroute.dto.response;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.PaymentStatus;
import com.spotroute.persistence.entity.Booking;
import com.spotroute.persistence.entity.Stop;
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
//    private String pickupPoint;
    private Stop pickupStop;
    private Stop destinationStop;
    private BigDecimal totalAmount;
    private BookingStatus status;
//    private String paymentReference;
    private LocalDateTime createdAt;

    public static BookingResponse from(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .ride(RideResponse.from(booking.getRide()))
                .seatCount(booking.getSeatCount())
                .pickupStop(booking.getPickupStop())
                .destinationStop(booking.getDestinationStop())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
