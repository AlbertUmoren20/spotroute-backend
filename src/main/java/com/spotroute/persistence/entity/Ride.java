package com.spotroute.persistence.entity;

import com.spotroute.core.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverProfile driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    @Builder.Default
    private int bookedSeats = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RideStatus status = RideStatus.SCHEDULED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    public int availableSeats() {
        return totalSeats - bookedSeats;
    }


}
