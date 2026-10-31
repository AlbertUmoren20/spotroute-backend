package com.spotroute.service;

import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.request.CreateRideRequest;
//import com.spotroute.Kafka.KafkaProducer;
import com.spotroute.dto.response.RideResponse;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.Ride;
import com.spotroute.persistence.entity.Route;
import com.spotroute.exception.ResourceNotFoundException;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.repository.RideRepository;
import com.spotroute.repository.RouteRepository;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;
//    private final KafkaProducer kafkaProducer;

    public List<RideResponse> getAvailableRides() {
        return rideRepository.findAvailableRides(LocalDateTime.now())
                .stream()
                .map(RideResponse::from)
                .toList();
    }

    @Transactional
    public RideResponse createRide(CreateRideRequest req ) {
       DriverProfile driver = SecurityUtil.getLoggedInDriver();
        if (driver == null) {
            throw new CustomException("Driver not found", HttpStatus.NOT_FOUND);
        }
        Route route = routeRepository.findById(req.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + req.getRouteId()));

        Ride ride = Ride.builder()
                .driver(driver)
                .route(route)
                .departureTime(req.getDepartureTime())
                .totalSeats(req.getTotalSeats())
                .build();
//        kafkaProducer.sendRide(req);
        return RideResponse.from(rideRepository.save(ride));
    }

    public List<RideResponse> getDriverRides() {
        DriverProfile driver = SecurityUtil.getLoggedInDriver();
        return rideRepository.findByDriverIdOrderByDepartureTimeDesc(driver.getId())
                .stream()
                .map(RideResponse::from)
                .toList();
    }

    public Ride findById(UUID id) {
        return rideRepository.findById(id.toString()).orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
    }
}
