package com.spotroute.service;

import com.spotroute.dto.response.LandMarkResponse;
import com.spotroute.entity.LandMark;
import com.spotroute.entity.Ride;
import com.spotroute.entity.Route;
import com.spotroute.exception.ResourceNotFoundException;
import com.spotroute.repository.LandMarkRepository;
import com.spotroute.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandMarkService {
    private final LandMarkRepository landMarkRepository;
    private final RouteRepository routeRepository;

    public List<LandMark> findLandMarkRouteId(Long routeId) {
        //To find by routeID if the route firstly exist
        //then the routeID should be = landmark
        Route route = routeRepository.findById(routeId).orElseThrow(() ->
                new ResourceNotFoundException("Route not found"));
        return landMarkRepository.findByRouteId(routeId);

    }
}
