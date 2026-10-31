package com.spotroute.core.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum DocumentType {
    DRIVERS_LICENSE_SCAN,
    PROOF_OF_OWNERSHIP,
    INSURANCE_CERTIFICATE,
    VEHICLE_CONDITION_VIDEO;

    public static final Set<DocumentType> REQUIRED_DOCUMENT_TYPES =
            Collections.unmodifiableSet(EnumSet.of(
                    DRIVERS_LICENSE_SCAN,
                    PROOF_OF_OWNERSHIP,
                    INSURANCE_CERTIFICATE,
                    VEHICLE_CONDITION_VIDEO));

    public boolean isVideo() {
        return this == VEHICLE_CONDITION_VIDEO;
    }
}