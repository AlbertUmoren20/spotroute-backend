package com.spotroute.dto.response;

import com.spotroute.core.enums.RideStatus;
import com.spotroute.entity.Ride;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RideResponse {
    private String id;
    private String driverId;
    private String driverName;
    private String driverCarModel;
    private String driverCarColor;
    private String driverCarPlate;
    private RouteResponse route;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int bookedSeats;
    private int availableSeats;
    private RideStatus status;
    private LocalDateTime createdAt;

    public static RideResponse from(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .driverId(ride.getDriver().getId())
                .driverName(ride.getDriver().getUser().getName())
                .driverCarModel(ride.getDriver().getCarModel())
                .driverCarColor(ride.getDriver().getCarColor())
                .driverCarPlate(ride.getDriver().getCarPlate())
                .route(RouteResponse.from(ride.getRoute()))
                .departureTime(ride.getDepartureTime())
                .totalSeats(ride.getTotalSeats())
                .bookedSeats(ride.getBookedSeats())
                .availableSeats(ride.availableSeats())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .build();
    }
}
