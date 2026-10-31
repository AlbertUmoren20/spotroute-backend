package com.spotroute.persistence.entity;

import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.core.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "driver_documents",
        uniqueConstraints = @UniqueConstraint(columnNames = {"driver_profile_id", "type"}))
@Getter
@Setter
@NoArgsConstructor
public class DriverDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_profile_id", nullable = false)
    private DriverProfile driverProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    /** Private storage key (or private URL), never a public link. */
    @Column(nullable = false, length = 512)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.PENDING;

    private String rejectionReason;
    private Instant reviewedAt;
    private String reviewedBy;

    @Column(nullable = false)
    private Instant uploadedAt = Instant.now();
}
