package com.spotroute.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {

    @NotNull(message = "Reset code is required")
    @Schema(description = "User Identification code for password reset", example = "MNIlknkNWLKnwWNLNIWNlkenlEIWNOenlwneknwenLKENW", requiredMode = Schema.RequiredMode.REQUIRED)
//    @JsonProperty("reset_string")
    private String resetString;

    @NotNull(message = "Password must be present for a change password operation")
//    @Size(min = 8, max = 100, message = "Password length is too short, minimum of eight (8) characters")
//    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-.]).{8,}$",
//            message = "Password must contain a least a number, uppercase and lower case, special character and minimum of 8 character")
    @Schema(description = "User's password credential", example = "Password@1", requiredMode = Schema.RequiredMode.REQUIRED)
//    @JsonProperty("new_password")
    private String newPassword;
}
