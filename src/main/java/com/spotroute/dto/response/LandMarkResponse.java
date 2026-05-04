package com.spotroute.dto.response;

import com.spotroute.entity.Booking;
import com.spotroute.entity.LandMark;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class LandMarkResponse {
    private List<String> name;
    private BigDecimal lat;
    private BigDecimal lng;
    private int sequenceOrder;

    public static LandMarkResponse from(LandMark landMaark) {
        return LandMarkResponse.builder()
                .name(landMaark.getName())
                .lat(landMaark.getLat())
                .lng(landMaark.getLng())
                .sequenceOrder(landMaark.getSequenceOrder())
                .build();
    }
}
