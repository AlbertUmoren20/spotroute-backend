package com.spotroute.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateBookingRequest {

        @NotNull(message = "Pickup stop is required")
        private UUID pickupStopId;

        @NotNull(message = "Destination stop is required")
        private UUID destinationStopId;

        @Min(value = 1, message = "Seat count must be at least 1")
        private int seatCount;
}
