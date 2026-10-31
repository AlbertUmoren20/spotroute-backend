package com.spotroute.dto.response;

import com.spotroute.core.enums.PositionRole;
import com.spotroute.persistence.entity.Position;

import java.time.Instant;
import java.util.UUID;

public record PositionResponse(
        UUID id,
        Double latitude,
        Double longitude,
        String formattedAddress,
        String placeId,
        PositionRole role,
        Instant createdAt
) {

    public static PositionResponse from(Position position) {
        return new PositionResponse(
                position.getId(),
                position.getCoordinates().getY(), // Y = latitude
                position.getCoordinates().getX(), // X = longitude
                position.getFormattedAddress(),
                position.getPlaceId(),
                position.getRole(),
                position.getCreatedAt()
        );
    }
}
