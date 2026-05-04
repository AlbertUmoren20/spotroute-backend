package com.spotroute.controller;

import com.spotroute.entity.LandMark;
import com.spotroute.service.LandMarkService;
import com.spotroute.util.AppUtil;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.LandMarkResponse;
import com.spotroute.dto.response.RouteResponse;
import com.spotroute.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;
    private final LandMarkService landMarkService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        List<RouteResponse> routes = routeRepository.findAll()
                .stream()
                .map(RouteResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(routes));
    }

    @GetMapping("/landmarks")
    public ResponseEntity<AppResponse<List<LandMark>>> getLandmarks(@RequestParam String routeId){
        AppUtil.setStartTime();
        List<LandMark> landMarks = landMarkService.findLandMarkRouteId(routeId);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<List<LandMark>> response = AppResponse.<List<LandMark>>builder()
                .status(HttpStatus.OK.toString())
                .message(" Route Landmarks successfully fetched!")
                .data(landMarks)
                .error("")
                .build();

        return ResponseEntity.ok(response);

    }

}
