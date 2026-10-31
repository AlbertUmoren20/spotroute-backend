package com.spotroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DriverApplyRequest{
    @NotBlank @Size(max = 50)
    String licenseNumber;
    @NotBlank @Size(max = 100)
    String vehicleModel;
    @NotBlank @Size(max = 50)
    String vehicleColor;
    @NotBlank @Size(max = 20)
    String vehiclePlate;
    // NIN is 11 digits, voter's card number up to 19. Adjust to your rules.
    @NotBlank @Pattern(regexp = "\\d{11,19}", message = "must be 11-19 digits")
    String governmentId;
    @NotBlank @Size(max = 50)
    String lasdriCardNumber;
    
    MultipartFile driversLicenseScan;

    MultipartFile proofOfOwnership;
    MultipartFile insuranceCertificate;

    MultipartFile vehicleConditionVideo;

}

