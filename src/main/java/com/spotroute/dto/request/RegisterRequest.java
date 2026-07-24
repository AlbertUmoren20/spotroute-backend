package com.spotroute.dto.request;

import com.spotroute.core.enums.Gender;
import com.spotroute.core.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "city is required")
    private String city;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Valid phone number required")
    private String phone;

    @NotNull(message = "Role is required (USER or DRIVER)")
    private Role role;

    // Driver-only fields (required when role = DRIVER)
    private String carModel;
    private String carPlate;
    private String carColor;
}
