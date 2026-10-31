package com.spotroute.controller;
import com.spotroute.config.ReviewKeyGuard;
import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.dto.request.ReviewRequest;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.PendingDocumentResponse;
import com.spotroute.service.DriverReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/driver-documents")
@RequiredArgsConstructor
public class DriverReviewController {

    private static final String classTag = "Driver Review (internal)";
    private static final String KEY_HEADER = "X-Review-Key";

    private final ReviewKeyGuard guard;
    private final DriverReviewService driverReviewService;

    @GetMapping("/pending")
    public List<PendingDocumentResponse> pending(
            @RequestHeader(value = KEY_HEADER, required = false) String key) {
        guard.check(key);
        return driverReviewService.pending();
    }

    @Operation(summary = "List drivers with their documents",
            description = "Oldest application first. Optional ?status=UNDER_REVIEW etc. Requires X-Review-Key.",
            tags = classTag)
    @GetMapping
    public AppResponse<?> drivers(
            @RequestHeader(value = KEY_HEADER, required = false) String key,
            @RequestParam(required = false) ApplicationStatus status) {
        guard.check(key);
        return AppResponse.builder()
                .status(HttpStatus.OK.toString())
                .message("Drivers retrieved successfully")
                .data(driverReviewService.listDrivers(status))
                .error("")
                .build();
    }
}
