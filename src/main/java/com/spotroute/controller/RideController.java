package com.spotroute.controller;

import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.request.CreateRideRequest;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.RideResponse;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.Ride;
import com.spotroute.service.RideService;
import com.spotroute.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<RideResponse>> createRide(
            @Valid @RequestBody CreateRideRequest req) {
        RideResponse ride = rideService.createRide(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Ride created", ride));
    }

    @Operation(summary = "User view their ride details", description = "" , tags = classTag)
    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<Ride>> getMyRides(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(rideService.findById(id)));
    }

    @Operation(summary = "Driver view their ride details", description = "" , tags = classTag)
    @GetMapping("/driver")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getDriverRides() {
        List<RideResponse> rides = rideService.getDriverRides();
        return ResponseEntity.ok(ApiResponse.ok(rides));
    }
}
