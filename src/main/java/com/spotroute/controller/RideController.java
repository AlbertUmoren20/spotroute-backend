package com.spotroute.controller;

import com.spotroute.dto.request.CreateRideRequest;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.RideResponse;
import com.spotroute.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final String classTag = "RideController";

    @Operation(summary = "Available rides for users", description = "" , tags = classTag)
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getAvailableRides() {
        return ResponseEntity.ok(ApiResponse.ok(rideService.getAvailableRides()));
    }

    @Operation(summary = "Drivers initiating the rides", description = "" , tags = classTag)
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<RideResponse>> createRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRideRequest req) {
        RideResponse ride = rideService.createRide(userDetails.getUsername(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Ride created", ride));
    }

    @Operation(summary = "User/Driver view their ride details", description = "" , tags = classTag)
    @GetMapping("/my")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getMyRides(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(rideService.getDriverRides(userDetails.getUsername())));
    }
}
