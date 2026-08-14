package com.spotroute.service;

import com.spotroute.Properties.JwtProperties;
import com.spotroute.core.enums.Role;
import com.spotroute.core.enums.Status;
import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.request.*;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.dto.response.DriverProfileResponse;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.User;
import com.spotroute.exception.BadRequestException;
import com.spotroute.persistence.token.RefreshToken;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.repository.RefreshTokenRepository;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.AppUtil;
import com.spotroute.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Driver;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;


    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .city(req.getCity())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .source(req.getSource())
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        return new AuthResponse(jwtUtil.generateToken(user), null, null);
    }


    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials!"));

        if(user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())){
            log.info("Failed login attempt" + req.getEmail(), user);
            throw new BadRequestException ("Invalid credentials!");
        }
        if(user.getStatus() != Status.ACTIVE){
            throw new BadRequestException("User is disabled");
        }

        String token = jwtUtil.generateToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
        refreshTokenRepository.save(refreshToken);

//        log.info("Logged in successfully " + user.getFirstName());
        return new AuthResponse (token, refreshTokenStr, jwtProperties.getRefreshExpiration());

    }

    public AuthResponse refreshToken(RefreshRequest req){
        var opt = refreshTokenRepository.findByToken(req.getRefreshToken()).orElseThrow(()->new RuntimeException("Invalid refresh token"));
        if(opt.isRevoked() || opt.getExpiryDate().isBefore(Instant.now())){
            throw new CustomException("Refresh token invalid or expired", HttpStatus.BAD_REQUEST);
        }
        User user = opt.getUser();
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        if (user.getStatus() != Status.ACTIVE) {
            throw new CustomException("User account is deactivated", HttpStatus.FORBIDDEN);
        }

        invalidateRefreshToken(req.getRefreshToken());
        String accessToken = jwtUtil.generateToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);
        opt.setToken(refreshTokenStr);
        opt.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
        refreshTokenRepository.save(opt);

        return new AuthResponse(accessToken,refreshTokenStr,jwtProperties.getRefreshExpiration() );
    }

    public void logout(LogoutRequest req){
        log.info("Logged out successfully");
        invalidateRefreshToken(req.getRefreshToken());

    }

    private void invalidateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public void changePassword(User loggedInUser, ChangePasswordRequest changePasswordRequest) throws Exception {
        if (loggedInUser == null) {
            throw new BadRequestException("Authenticated User not found");
        }
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), loggedInUser.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword())) {
            throw new BadRequestException("New password cannot be the same as old password");
        }
        loggedInUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(loggedInUser);
    }



    public void initiatePasswordReset(String email) throws IOException {
        String token = AppUtil.generateVerificationCode();
        Date expirationTime = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10));
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User not found for email: {}", email);
            return new CustomException("Password reset instructions have been sent to the provided email address.", HttpStatus.OK);
        });
        user.setPasswordResetExpiryDate(expirationTime);
        user.setPasswordResetStr(token);
        userRepository.save(user);

        // GENERATE RESET TOKEN AND SEND AN EMAIL
//        String url =  "/reset-password?token=" + token;
//        String link = "<a href=" + url + ">RESET PASSWORD</a>";
//        String body = "Kindly click on the below link to reset your password.<br> " +
//                "<b>Link</b> : " + link + "<br>";
//
//        mailNotificationService.sendEmail("RESET PASSWORD", user.getEmail(), user.getFirstName(), body);
    }

    public void validateResetToken(String resetPasswordToken) throws IOException {
        User user = userRepository.findByPasswordResetStr(resetPasswordToken)
                .orElseThrow(() -> new CustomException("Invalid reset token", HttpStatus.BAD_REQUEST));

        if (user.getPasswordResetExpiryDate().compareTo(new Date()) < 0) {
            throw new CustomException("The reset link has expired", HttpStatus.BAD_REQUEST);
        }
    }



    public void resetPassword(ResetPasswordRequest resetPasswordRequest) throws IOException {
        User user = userRepository.findByPasswordResetStr(resetPasswordRequest.getResetString())
                .orElseThrow(() -> new CustomException("Invalid reset token", HttpStatus.BAD_REQUEST));

        if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(), user.getPassword())) {
            throw new CustomException("Password still in use", HttpStatus.BAD_REQUEST);
        }
        if (user.getPasswordResetExpiryDate().compareTo(new Date()) >= 0) {
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        } else {
            throw new CustomException("The reset link has expired", HttpStatus.BAD_REQUEST);
        }
        user.setPasswordResetStr(null);
        user.setPasswordResetExpiryDate(null);
        userRepository.save(user);
    }
}


//    public AuthResponse getMe(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BadRequestException("User not found"));
//
//        DriverProfile driverProfile = null;
//        if (user.getRole() == Role.DRIVER) {
//            driverProfile = driverProfileRepository.findByUserId(user.getId()).orElse(null);
//        }
//
//        return buildAuthResponse(null, refreshTokenStr, user, driverProfile);
//    }

