package com.spotroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentInitRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    // Redirect URL after Flutterwave hosted payment
    private String redirectUrl;
}
