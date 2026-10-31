package com.spotroute.persistence.entity;

import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.core.enums.DocumentType;
import com.spotroute.core.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.spotroute.core.enums.DocumentType.REQUIRED_DOCUMENT_TYPES;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @Column(nullable = false) private String licenseNumber;
    @Column(nullable = false) private String governmentId;
    @Column(nullable = false) private String lasdriCardNumber;
    @Column(nullable = false) private String vehicleModel;
    @Column(nullable = false) private String vehicleColor;
    @Column(nullable = false) private String vehiclePlate;

    private Instant approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DriverStatus status = DriverStatus.ACTIVE;

    @OneToMany(mappedBy = "driverProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DriverDocument> documents = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Ride> rides;

    public void addDocument(DriverDocument document) {
        documents.add(document);
        document.setDriverProfile(this);
    }

    public Optional<DriverDocument> findDocument(DocumentType type) {
        return documents.stream()
                .filter(d -> d.getType() == type)
                .findFirst();
    }

    /**
     * Derives applicationStatus from the document statuses.
     * SUSPENDED is never overwritten.
     *  - any REJECTED document           -> REJECTED
     *  - all required documents APPROVED -> APPROVED
     *  - otherwise                       -> UNDER_REVIEW
     */
    public void recalculateProfileStatus() {
        if (applicationStatus == ApplicationStatus.SUSPENDED) {
            return;
        }

        if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
            applicationStatus = ApplicationStatus.REJECTED;
            approvedAt = null;
            return;
        }

        Set<DocumentType> approved = documents.stream()
                .filter(d -> d.getStatus() == DocumentStatus.APPROVED)
                .map(DriverDocument::getType)
                .collect(Collectors.toSet());

        if (approved.containsAll(REQUIRED_DOCUMENT_TYPES)) {
            applicationStatus = ApplicationStatus.APPROVED;
            if (approvedAt == null) {
                approvedAt = Instant.now();
            }
            return;
        }
        applicationStatus = ApplicationStatus.UNDER_REVIEW;
        approvedAt = null;
    }
}