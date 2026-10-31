package com.spotroute.dto.response;

import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.core.enums.DocumentType;

import java.time.Instant;
import java.util.List;

public record ReviewResponse(
        String profileId,
        String email,
        ApplicationStatus applicationStatus,
        String licenseNumber,
        String vehicleModel,
        String vehicleColor,
        String vehiclePlate,
        String governmentId,
        String lasdriCardNumber,
        Instant createdAt,
        Instant approvedAt,
        List<DocumentReview> documents) {

    public record DocumentReview(
            Long id,
            DocumentType type,
            DocumentStatus status,
            String fileUrl,
            String rejectionReason,
            Instant uploadedAt,
            Instant reviewedAt,
            String reviewedBy) {}
}
