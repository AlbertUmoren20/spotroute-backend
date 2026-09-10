package com.spotroute.controller;

import com.spotroute.dto.request.*;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.persistence.entity.User;
import com.spotroute.service.AuthService;
import com.spotroute.util.AppUtil;
import com.spotroute.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final String classTag = "AuthController";

    @Operation(summary = "User Registration", description = "", tags = classTag)
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

    @Operation(summary = "User Login", description = "", tags = classTag)
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

    @Operation(summary = "Refresh Token", description = "Refresh access token with refresh token.", tags = classTag)
    @PostMapping("/refresh")
    public ResponseEntity<AppResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
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

    @Operation(summary = "Logout", description = "Logout with refresh token.",tags = classTag)
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


    @Operation(summary = "Change Password", description = "Changed Password as a logged in user.", tags = classTag)
    @PostMapping("/change-password")
    public ResponseEntity<AppResponse<Void>> changePassword(@AuthenticationPrincipal(errorOnInvalidType = true) UserDetails userDetails,
                                                            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
        User user = SecurityUtil.getLoggedInUserFromContext();
        log.info("User from the change password controller: {}", user);
        AppUtil.setStartTime();
        authService.changePassword(user, changePasswordRequest);
        String formattedExecTime = AppUtil.stopTimer();
        return ResponseEntity.ok().body(AppResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Password changed successfully")
                .data(null)
                .execTime(formattedExecTime)
                .error("").build());
    }

    @Operation(summary = "Initiate Password Reset", description = "Initiates password reset for the corresponding email address", tags = classTag)
    @PostMapping(value = "/initiate-password-reset")
    public ResponseEntity<AppResponse<Void>> initiatePasswordReset(@Valid @RequestBody InitiatePasswordResetRequest initiatePasswordResetRequest) throws Exception {
        AppUtil.setStartTime();
        authService.initiatePasswordReset(initiatePasswordResetRequest.getEmail());
        String formattedExecTime = AppUtil.stopTimer();
        return ResponseEntity.ok().body(AppResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Password reset initiated successfully")
                .data(null)
                .execTime(formattedExecTime)
                .error("").build());
    }

    @Operation(summary = "Validate reset token for password Reset", description = "Validate reset token for password Reset", tags = classTag)
    @GetMapping(value = "/validate-reset-token/{resetPasswordToken}")
    public ResponseEntity<AppResponse<Void>> validateResetToken(@PathVariable String resetPasswordToken) throws IOException {
        AppUtil.setStartTime();
        authService.validateResetToken(resetPasswordToken);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<Void> response = AppResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Token validated successfully")
                .data(null)
                .execTime(formattedExecTime)
                .error("").build();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Reset Password", description = "Resets password to the corresponding email address", tags = classTag)
    @PostMapping(value = "/reset-password")
    //@PreAuthorize("@permissionService.hasPermission('UPDATE_USER')")
    public ResponseEntity<AppResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) throws IOException {
        AppUtil.setStartTime();
        authService.resetPassword(resetPasswordRequest);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<Void> response = AppResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Password set successfully.")
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
