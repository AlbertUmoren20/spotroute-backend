package com.spotroute.controller;

import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.RouteResponse;
import com.spotroute.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        List<RouteResponse> routes = routeRepository.findAll()
                .stream()
                .map(RouteResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(routes));
    }
}
