package com.spotroute.dto.response;

import com.spotroute.entity.Route;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RouteResponse {
    private String id;
    private String origin;
    private String destination;
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
