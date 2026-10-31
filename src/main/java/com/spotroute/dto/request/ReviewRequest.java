package com.spotroute.dto.request;

import com.spotroute.core.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReviewRequest{
        @NotNull
        DocumentStatus decision;
        String rejectionReason;
        @NotBlank String reviewer;
}
