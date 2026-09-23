package com.spotroute.controller;

import com.spotroute.dto.response.AppResponse;
import com.spotroute.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    private final String classTag = "File Controller";

    @Operation(summary = "Upload profile picture", description = "Upload profile picture", tags = classTag)
    @PostMapping(value = "/upload/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppResponse<?> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Uploading logo");
        AppResponse<?> response =  fileService.storeImage(file);
        response.setStatus(HttpStatus.OK.toString());
        response.setMessage("Logo has successfully been uploaded.");
        response.setData(null);
        return response;
    }

}

