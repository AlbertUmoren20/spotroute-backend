package com.spotroute.controller;

import com.spotroute.entity.LandMark;
import com.spotroute.service.LandMarkService;
import com.spotroute.util.AppUtil;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.LandMarkResponse;
import com.spotroute.dto.response.RouteResponse;
import com.spotroute.repository.RouteRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;
    private final LandMarkService landMarkService;
    private final String classTag = "RouteController";

    @GetMapping
    @Operation(summary = "Users to get all routes", description = "" , tags = classTag)
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        List<RouteResponse> routes = routeRepository.findAll()
                .stream()
                .map(RouteResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(routes));
    }

    @Operation(summary = "User/Driver view landmarks connected to their route", description = "For each route, there are landmarks" , tags = classTag)
    @GetMapping("/{routeId}/landmarks")
    public ResponseEntity<AppResponse<List<LandMark>>> getLandmarks(@PathVariable Long routeId){
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
