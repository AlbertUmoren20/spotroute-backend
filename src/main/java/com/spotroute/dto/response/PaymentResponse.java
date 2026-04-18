package com.spotroute.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentReference;
    private String paymentLink;
    private String status;
    private String message;
}
