package com.spotroute.dto.response;

import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.core.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse{
    private DocumentType type;
    private DocumentStatus status;
    private  String rejectionReason;
    private   Instant uploadedAt;
    private  Instant reviewedAt;
}
