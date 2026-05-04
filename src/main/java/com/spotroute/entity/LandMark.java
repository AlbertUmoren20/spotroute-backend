package com.spotroute.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Entity
@Table(name = "landmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandMark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "name")
    private List<String> name;

    @Column(nullable = false)
    private BigDecimal lat;

    @Column(nullable = false)
    private BigDecimal lng;

    @Column(nullable = false)
    private int sequenceOrder;
}
