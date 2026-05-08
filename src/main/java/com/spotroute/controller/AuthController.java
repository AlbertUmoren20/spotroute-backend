package com.spotroute.controller;

import com.spotroute.dto.request.LoginRequest;
import com.spotroute.dto.request.RegisterRequest;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final String classTag = "AuthController";

    @Operation(summary = "User/Driver Registration", description = "" , tags = classTag)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registration successful", response));
    }

    @Operation(summary = "User/Driver Login", description = "" , tags = classTag)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @Operation(summary = "User/Driver Details", description = "" , tags = classTag)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        AuthResponse response = authService.getMe(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
