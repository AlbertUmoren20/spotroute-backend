package com.spotroute.dto.response;

import com.spotroute.persistence.entity.Location;
import com.spotroute.persistence.entity.Route;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RouteResponse {
    private Long id;
    private Location origin;
    private Location destination;
    private BigDecimal pricePerSeat;
    private String description;
    private List<String> pickupPoints;

    public static RouteResponse from(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .pricePerSeat(route.getPricePerSeat())
                .description(route.getDescription())
                .pickupPoints(route.getPickupPoints())
                .build();
    }
}
