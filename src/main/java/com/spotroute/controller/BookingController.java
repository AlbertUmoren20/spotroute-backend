package com.spotroute.controller;

import com.spotroute.dto.request.CreateBookingRequest;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.dto.response.BookingResponse;
import com.spotroute.service.BookingService;
import com.spotroute.util.AppUtil;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings/v1")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String classTag = "BookingController";

    @Operation(summary = "Create Booking", description = "Users(Drivers) are able to create booking" , tags = classTag)
    @PostMapping
//    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBookingRequest req) {
        AppUtil.setStartTime();
        BookingResponse booking = bookingService.createBooking(userDetails.getUsername(), req);
        AppUtil.stopTimer();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Booking created", booking));
    }

    @Operation(summary = "Get User booking history", description = "Users are ale to send a post request to view their ride history" , tags = classTag)
    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getUserBookings(userDetails.getUsername())));
    }

    @Operation(summary = "User's booking details", description = "Users booking details" , tags = classTag)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBooking(id, userDetails.getUsername())));
    }
}
