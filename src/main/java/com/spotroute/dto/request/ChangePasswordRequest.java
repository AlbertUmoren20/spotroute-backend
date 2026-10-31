package com.spotroute.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotNull(message = "Password must be present for a change password operation")
    @Schema(description = "User's password credential", example = "Password@1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

}
