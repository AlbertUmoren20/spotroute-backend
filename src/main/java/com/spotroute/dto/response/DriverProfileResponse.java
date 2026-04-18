package com.spotroute.dto.response;

import com.spotroute.entity.DriverProfile;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DriverProfileResponse {
    private String id;
    private String carModel;
    private String carPlate;
    private String carColor;
    private BigDecimal walletBalance;
    private DriverProfile.DriverStatus status;

    public static DriverProfileResponse from(DriverProfile dp) {
        if (dp == null) return null;
        return DriverProfileResponse.builder()
                .id(dp.getId())
                .carModel(dp.getCarModel())
                .carPlate(dp.getCarPlate())
                .carColor(dp.getCarColor())
                .walletBalance(dp.getWalletBalance())
                .status(dp.getStatus())
                .build();
    }
}
