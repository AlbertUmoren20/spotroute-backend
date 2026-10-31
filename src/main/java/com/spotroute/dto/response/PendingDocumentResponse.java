package com.spotroute.dto.response;

import com.spotroute.core.enums.DocumentType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class PendingDocumentResponse {
         private Long documentId;
         private String driverEmail;
         private String licenseNumber;
         private DocumentType type;
         private String fileUrl;
         private Instant uploadedAt;
}
