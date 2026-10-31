package com.spotroute.dto.response;

import com.spotroute.persistence.entity.Stop;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StopResponse {
    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer sequenceOrder;

    public static StopResponse from(Stop stop) {
        return StopResponse.builder()
                .id(stop.getId())
                .name(stop.getName())
                .latitude(stop.getCoordinates().getY())
                .longitude(stop.getCoordinates().getX())
                .sequenceOrder(stop.getSequenceOrder())
                .build();
    }
}