package com.spotroute.dto.request;

import com.spotroute.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Valid phone number required")
    private String phone;

    @NotNull(message = "Role is required (USER or DRIVER)")
    private User.Role role;

    // Driver-only fields (required when role = DRIVER)
    private String carModel;
    private String carPlate;
    private String carColor;
}
