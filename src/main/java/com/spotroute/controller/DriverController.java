package com.spotroute.controller;

import com.spotroute.core.enums.DocumentType;
import com.spotroute.dto.request.DriverApplyRequest;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.service.DriverApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

        private static final String classTag = "Driver Onboarding";
        private final DriverApplicationService service;

        @Operation(summary = "Apply to become a driver",
                description = "Submit vehicle/licence details with driversLicenseScan, proofOfOwnership, "
                        + "insuranceCertificate and vehicleConditionVideo", tags = classTag)
        @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public AppResponse<?> apply(@Valid DriverApplyRequest request) {
            log.info("Driver application received");
            return AppResponse.builder()
                    .status(HttpStatus.OK.toString())
                    .message("Driver application submitted successfully")
                    .data(service.apply(request))
//                    .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                    .error("")
                    .build();
        }
//
//        @Operation(summary = "Get my driver application",
//                description = "Application status and per-document status/rejection reason", tags = classTag)
//        @GetMapping("/application")
//        public AppResponse<?> application() {
//            return AppResponse.builder()
//                    .status(HttpStatus.OK.toString())
//                    .message("Driver application retrieved successfully")
//                    .data(service.getApplication())
//                    .error("")
//                    .build();
//        }

        @Operation(summary = "Re-upload a rejected document",
                description = "Only allowed when that document has been REJECTED", tags = classTag)
        @PutMapping(value = "/documents/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public AppResponse<?> reupload(@PathVariable DocumentType type,
                                       @RequestParam("file") MultipartFile file) {
            log.info("Re-uploading driver document {}", type);
            return AppResponse.builder()
                    .status(HttpStatus.OK.toString())
                    .message("Document re-uploaded successfully")
                    .data(service.reuploadDocument(type, file))
                    .error("")
                    .build();
        }
    }
