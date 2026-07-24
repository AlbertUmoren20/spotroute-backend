package com.spotroute.controller;

import com.spotroute.dto.request.LoginRequest;
import com.spotroute.dto.request.LogoutRequest;
import com.spotroute.dto.request.RefreshRequest;
import com.spotroute.dto.request.RegisterRequest;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.service.AuthService;
import com.spotroute.util.AppUtil;
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
    public ResponseEntity<AppResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AppUtil.setStartTime();
        AuthResponse response = authService.register(req);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<AuthResponse> appResponse = AppResponse.<AuthResponse>builder()
                .status("success")
                .message("Registration successful")
                .data(response)
                .execTime(formattedExecTime)
                .build();
        return ResponseEntity.ok().body(appResponse);
    }

    @Operation(summary = "User/Driver Login", description = "" , tags = classTag)
    @PostMapping("/login")
    public ResponseEntity<AppResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        AppUtil.setStartTime();
        AuthResponse response = authService.login(req);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<AuthResponse> appResponse = AppResponse.<AuthResponse>builder()
                .status("success")
                .message("Login successful")
                .data(response)
                .execTime(formattedExecTime)
                .build();
        return ResponseEntity.ok().body(appResponse);
    }

    @Operation(summary = "Refresh Token", description="Refresh access token with refresh token.", tags= "Auth Controller")
    @PostMapping("/refresh")
    public ResponseEntity<AppResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req){
        AppUtil.setStartTime();
        AuthResponse refreshResponse = authService.refreshToken(req);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<AuthResponse> response = AppResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Token refreshed successfully")
                .data(refreshResponse)
                .execTime(formattedExecTime)
                .error("").build();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Logout", description ="Logout with refresh token.", tags= "Auth Controller")
    @PostMapping("/logout")
    public ResponseEntity<AppResponse<Void>> logout(@Valid @RequestBody LogoutRequest req) {
        AppUtil.setStartTime();
        authService.logout(req);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<Void> response = AppResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Logged out successfully")
                .data(null)
                .execTime(formattedExecTime)
                .error("").build();
        return ResponseEntity.ok().body(response);
    }
}

//@Operation(summary = "User/Driver Details", description = "" , tags = classTag)
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<AuthResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
//        AuthResponse response = authService.getMe(userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.ok(response));
//    }
//}
