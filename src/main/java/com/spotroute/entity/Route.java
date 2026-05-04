package com.spotroute.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSeat;

    @Column(nullable = false)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "pickup_point")
    private List<String> pickupPoints;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "landmarks")
    private List<String> landmarks;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<Ride> rides;
}
