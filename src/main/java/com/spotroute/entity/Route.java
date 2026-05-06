package com.spotroute.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<Ride> rides;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LandMark> landmarks = new ArrayList<>(); // Initialize to prevent NullPointerException
}
