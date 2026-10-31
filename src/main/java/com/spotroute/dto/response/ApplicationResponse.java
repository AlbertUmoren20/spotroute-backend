package com.spotroute.dto.response;

import com.spotroute.core.enums.ApplicationStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private ApplicationStatus applicationStatus;
    private Instant createdAt;
    private Instant approvedAt;
    private List<DocumentResponse> documents;
}

