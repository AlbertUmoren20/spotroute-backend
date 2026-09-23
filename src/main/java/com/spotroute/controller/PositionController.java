package com.spotroute.controller;

import com.spotroute.core.enums.PositionRole;
import com.spotroute.dto.request.CreateBookingRequest;
import com.spotroute.dto.request.PositionRequest;
import com.spotroute.dto.response.*;
import com.spotroute.persistence.entity.LandMark;
import com.spotroute.persistence.entity.Position;
import com.spotroute.service.PositionService;
import com.spotroute.util.AppUtil;
import com.sun.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/positions")
@RequiredArgsConstructor
public class PositionController {
    private final String classTag = "PostionController";
    private final PositionService positionService;

    @Operation(summary = "", description = "" , tags = classTag)
    @PostMapping
    public ResponseEntity<AppResponse<PositionResponse>> save(@Valid @RequestBody PositionRequest request) {
        AppUtil.setStartTime();
        PositionResponse position = positionService.savePosition(request);
        String formattedExecTime = AppUtil.stopTimer();
        AppResponse<PositionResponse> appResponse = AppResponse.<PositionResponse>builder()
                .status("success")
                .message("Position registered successful")
                .data(position)
                .execTime(formattedExecTime)
                .build();
        return ResponseEntity.ok().body(appResponse);
    }
    }


