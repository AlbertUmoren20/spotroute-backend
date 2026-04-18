package com.spotroute.dto.response;

import com.spotroute.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String name;
    private String email;
    private String phone;
    private User.Role role;
    private DriverProfileResponse driverProfile;
}
