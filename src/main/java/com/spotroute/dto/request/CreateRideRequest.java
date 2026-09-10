package com.spotroute.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRideRequest {

    @NotNull(message = "ID cannot be null")
    private Long routeId;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @Min(value = 1, message = "Total seats must be at least 1")
    @Max(value = 4, message = "Total seats cannot exceed 4")
    private int totalSeats;
}
