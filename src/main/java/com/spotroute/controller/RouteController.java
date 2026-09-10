package com.spotroute.controller;

import com.spotroute.core.exceptions.CustomException;
import com.spotroute.persistence.entity.LandMark;
import com.spotroute.persistence.entity.User;
import com.spotroute.service.LandMarkService;
import com.spotroute.util.AppUtil;
import com.spotroute.dto.response.ApiResponse;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.dto.response.RouteResponse;
import com.spotroute.repository.RouteRepository;
import com.spotroute.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes/v1")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;
    private final LandMarkService landMarkService;
    private final String classTag = "Route Controller";

    @GetMapping
    @Operation(summary = "Users to get all routes", description = "" , tags = classTag)
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        User user = SecurityUtil.getLoggedInUserFromContext();
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
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
