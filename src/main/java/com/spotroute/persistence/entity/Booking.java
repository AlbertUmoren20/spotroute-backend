package com.spotroute.persistence.entity;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private int seatCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pickup_stop_id", nullable = false)
    private Stop pickupStop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_stop_id", nullable = false)
    private Stop destinationStop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    // Payment tracking
//    private String paymentReference;
//    private String flutterwaveTransactionId;
//
//    @Enumerated(EnumType.STRING)
//    @Builder.Default
//    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
