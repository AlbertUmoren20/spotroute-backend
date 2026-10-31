package com.spotroute.service;

import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.dto.request.ReviewRequest;
import com.spotroute.dto.response.PendingDocumentResponse;
import com.spotroute.dto.response.ReviewResponse;
import com.spotroute.persistence.entity.DriverDocument;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.repository.DriverDocumentRepository;
import com.spotroute.repository.DriverProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverReviewService {

    private final DriverProfileRepository profileRepository;
    private final DriverDocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<PendingDocumentResponse> pending() {
        return documentRepository.findAllByStatusWithDriver(DocumentStatus.PENDING).stream()
                .map(d -> new PendingDocumentResponse(
                        d.getId(),
                        d.getDriverProfile().getUser().getEmail(),
                        d.getDriverProfile().getLicenseNumber(),
                        d.getType(),
                        d.getFileUrl(),
                        d.getUploadedAt()))
                .toList();
    }

//    @Transactional
//    public ReviewResponse review(Long documentId, ReviewRequest req) {
//        if (req.decision() == DocumentStatus.PENDING) {
//            throw new BadRequestException("Decision must be APPROVED or REJECTED");
//        }
//        boolean rejecting = req.decision() == DocumentStatus.REJECTED;
//        if (rejecting && (req.rejectionReason() == null || req.rejectionReason().isBlank())) {
//            throw new BadRequestException("A rejection reason is required when rejecting a document");
//        }
//
//        // Lock the profile first, then read documents, so recalculation sees fresh state.
//        DriverProfile profile = profileRepository.findByDocumentIdForUpdate(documentId)
//                .orElseThrow(() -> new NotFoundException("Document " + documentId + " not found"));
//        DriverDocument doc = profile.getDocuments().stream()
//                .filter(d -> d.getId().equals(documentId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException(
//                        "Document " + documentId + " missing from its own profile " + profile.getId()));
//
//        // Relax this check if reviewers need to overturn earlier decisions.
//        if (doc.getStatus() != DocumentStatus.PENDING) {
//            throw new com.spotroute.exception.BadRequestException("Document " + documentId + " has already been reviewed ("
//                    + doc.getStatus() + ")");
//        }
//
//        doc.setStatus(req.decision());
//        doc.setRejectionReason(rejecting ? req.rejectionReason().trim() : null);
//        doc.setReviewedBy(req.reviewer().trim());
//        doc.setReviewedAt(Instant.now());
//
//        profile.recalculateProfileStatus();
//        return new ReviewResponse(doc.getId(), doc.getStatus(), profile.getApplicationStatus());
//    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listDrivers(ApplicationStatus status) {
        List<DriverProfile> profiles = status == null
                ? profileRepository.findAllByOrderByCreatedAtAsc()
                : profileRepository.findAllByApplicationStatusOrderByCreatedAtAsc(status);
        return profiles.stream().map(DriverReviewService::toReviewResponse).toList();
    }

    private static ReviewResponse toReviewResponse(DriverProfile p) {
        List<ReviewResponse.DocumentReview> documents = p.getDocuments().stream()
                .sorted(Comparator.comparing(DriverDocument::getType))
                .map(d -> new ReviewResponse.DocumentReview(
                        d.getId(),
                        d.getType(),
                        d.getStatus(),
                        d.getFileUrl(),
                        d.getRejectionReason(),
                        d.getUploadedAt(),
                        d.getReviewedAt(),
                        d.getReviewedBy()))
                .toList();

        return new ReviewResponse(
                p.getId(),
                p.getUser().getEmail(),
                p.getApplicationStatus(),
                p.getLicenseNumber(),
                p.getVehicleModel(),
                p.getVehicleColor(),
                p.getVehiclePlate(),
                p.getGovernmentId(),
                p.getLasdriCardNumber(),
                p.getCreatedAt(),
                p.getApprovedAt(),
                documents);
    }
}