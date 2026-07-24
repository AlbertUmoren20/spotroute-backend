package com.spotroute.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spotroute.core.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String carModel;

    @Column(nullable = false)
    private String carPlate;

    @Column(nullable = false)
    private String carColor;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @JsonIgnore
    private DriverStatus status = DriverStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Ride> rides;

}
