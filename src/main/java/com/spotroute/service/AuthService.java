package com.spotroute.service;

import com.spotroute.Properties.JwtProperties;
import com.spotroute.core.enums.Role;
import com.spotroute.core.enums.Status;
import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.request.LoginRequest;
import com.spotroute.dto.request.LogoutRequest;
import com.spotroute.dto.request.RefreshRequest;
import com.spotroute.dto.request.RegisterRequest;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.dto.response.DriverProfileResponse;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.User;
import com.spotroute.exception.BadRequestException;
import com.spotroute.persistence.token.RefreshToken;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.repository.RefreshTokenRepository;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


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

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        if (req.getRole() == Role.DRIVER) {
            if (req.getCarModel() == null || req.getCarPlate() == null || req.getCarColor() == null) {
                throw new BadRequestException("Car details are required for driver registration");
            }
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
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        DriverProfile driverProfile = null;
        if (req.getRole() == Role.DRIVER) {
            driverProfile = DriverProfile.builder()
                    .user(user)
                    .carModel(req.getCarModel())
                    .carPlate(req.getCarPlate())
                    .carColor(req.getCarColor())
                    .build();
            driverProfileRepository.save(driverProfile);
        }
        return new AuthResponse( jwtUtil.generateToken(user), null, null);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException(null));

        if(user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())){
            log.info("Failed login attempt" + req.getEmail(), user);
            throw new BadRequestException ("Invalid credentials!");
        }
        if(user.getStatus() != Status.ACTIVE){
            throw new BadRequestException("User is disabled");
        }
        DriverProfile driverProfile = null;
        if (user.getRole() == Role.DRIVER) {
            driverProfile = driverProfileRepository.findByUserId(user.getId()).orElse(null);
        }

        String token = jwtUtil.generateToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse (token, refreshTokenStr, jwtProperties.getRefreshExpiration());

    }

    public AuthResponse refreshToken(RefreshRequest req){
        var opt = refreshTokenRepository.findByToken(req.getRefreshToken()).orElseThrow(()->new RuntimeException("Invalid refresh token"));
        if(opt.isRevoked() || opt.getExpiryDate().isBefore(Instant.now())){
            throw new CustomException("Refresh token invalid or expired", HttpStatus.BAD_REQUEST);
        }
        User user = opt.getUser();
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
        invalidateRefreshToken(req.getRefreshToken());
    }

    private void invalidateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
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

}
