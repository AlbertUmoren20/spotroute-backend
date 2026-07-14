package com.spotroute.service;

import com.spotroute.core.enums.Status;
import com.spotroute.dto.request.LoginRequest;
import com.spotroute.dto.request.RegisterRequest;
import com.spotroute.dto.response.AuthResponse;
import com.spotroute.dto.response.DriverProfileResponse;
import com.spotroute.entity.DriverProfile;
import com.spotroute.entity.User;
import com.spotroute.exception.BadRequestException;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        if (req.getRole() == User.Role.DRIVER) {
            if (req.getCarModel() == null || req.getCarPlate() == null || req.getCarColor() == null) {
                throw new BadRequestException("Car details are required for driver registration");
            }
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(req.getRole())
                .build();
        userRepository.save(user);

        DriverProfile driverProfile = null;
        if (req.getRole() == User.Role.DRIVER) {
            driverProfile = DriverProfile.builder()
                    .user(user)
                    .carModel(req.getCarModel())
                    .carPlate(req.getCarPlate())
                    .carColor(req.getCarColor())
                    .build();
            driverProfileRepository.save(driverProfile);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return buildAuthResponse(token, user, driverProfile);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("User account is deactivated"));

        if(user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())){
            log.info("Failed login attempt" + req.getEmail(), user);
            throw new BadRequestException ("Invalid credentials!");
        }
        if(user.getStatus() != Status.ACTIVE)
            throw new BadRequestException("User is disabled");

        DriverProfile driverProfile = null;
        if (user.getRole() == User.Role.DRIVER) {
            driverProfile = driverProfileRepository.findByUserId(user.getId()).orElse(null);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return buildAuthResponse(token, user, driverProfile);
    }

    public AuthResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        DriverProfile driverProfile = null;
        if (user.getRole() == User.Role.DRIVER) {
            driverProfile = driverProfileRepository.findByUserId(user.getId()).orElse(null);
        }

        return buildAuthResponse(null, user, driverProfile);
    }

    private AuthResponse buildAuthResponse(String token, User user, DriverProfile dp) {
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .driverProfile(DriverProfileResponse.from(dp))
                .build();
    }
}
