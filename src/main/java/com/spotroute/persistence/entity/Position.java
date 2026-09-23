package com.spotroute.persistence.entity;

import com.spotroute.core.enums.PositionRole;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point coordinates;

    private String formattedAddress;
    private String placeId;

    @Enumerated(EnumType.STRING)
    private PositionRole role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;

}