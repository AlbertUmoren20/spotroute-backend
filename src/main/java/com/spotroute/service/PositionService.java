    package com.spotroute.service;

    import com.spotroute.core.enums.Status;
    import com.spotroute.core.exceptions.CustomException;
    import com.spotroute.dto.request.PositionRequest;
    import com.spotroute.dto.response.PositionResponse;
    import com.spotroute.persistence.entity.Position;
    import com.spotroute.persistence.entity.User;
    import com.spotroute.repository.PositionRepository;
    import com.spotroute.repository.StopRepository;
    import com.spotroute.util.SecurityUtil;
    import jakarta.servlet.ServletContext;
    import jakarta.validation.Valid;
    import lombok.AllArgsConstructor;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.locationtech.jts.geom.Coordinate;
    import org.locationtech.jts.geom.GeometryFactory;
    import org.locationtech.jts.geom.Point;
    import org.locationtech.jts.geom.PrecisionModel;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.time.Instant;

    @Service
    @AllArgsConstructor
    @Slf4j
    public class PositionService {

        private static final double DEFAULT_MATCH_RADIUS_METERS = 500.0;
        private final StopRepository stopRepository;
        private final PositionRepository positionRepository;
        private final GeometryFactory geometryFactory;


        public PositionResponse savePosition(PositionRequest request) {
                User user = SecurityUtil.getLoggedInUserFromContext();
                if (user.getStatus() != Status.ACTIVE) {
                    throw new CustomException("User account is deactivated", HttpStatus.FORBIDDEN);
                }
                Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
                Position position = new Position();
                position.setCoordinates(point);
                position.setFormattedAddress(request.getFormattedAddress());
                position.setRole(request.getRole());
                position.setPlaceId(request.getPlaceId());
                position.setUser(user);
                position.setCreatedAt(Instant.now());

            stopRepository.findNearestStop(point, DEFAULT_MATCH_RADIUS_METERS)
                    .ifPresent(stop -> position.setMatchedStopId(stop.getId()));

                log.info("Postions successfully captured", request.getFormattedAddress(), user);
                Position savedPosition = positionRepository.save(position);
                return PositionResponse.from(savedPosition);
        }
    }
