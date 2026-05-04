package com.spotroute.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookingRequest {

    @NotBlank(message = "Ride ID is required")
    @JsonIgnore
    private String rideId;

    @Min(value = 1, message = "Seat count must be at least 1")
    private int seatCount;

    @NotBlank(message = "Pickup point is required")
    private String pickupPoint;
}
