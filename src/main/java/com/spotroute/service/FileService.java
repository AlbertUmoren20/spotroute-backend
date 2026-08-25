package com.spotroute.service;


import com.spotroute.core.enums.Status;
import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.exception.BadRequestException;
import com.spotroute.persistence.entity.User;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;

    public static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/jpg"
    ));
    public static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "png", "jpg", "jpeg"
    ));

    public AppResponse<?> storeImage(MultipartFile file) throws IOException {
        User user = SecurityUtil.getLoggedInUserFromContext();
        if (user.getStatus() != Status.ACTIVE) {
            throw new CustomException("User account is deactivated", HttpStatus.FORBIDDEN);
        }

        long fileSize = file.getSize();
        long maxAllowedSize = 5 * 1024 * 1024; // 5 MB
        if (fileSize == 0) {
            throw new CustomException("File is empty" + file.getOriginalFilename(), HttpStatus.BAD_REQUEST);
        }
        if (fileSize > maxAllowedSize) {
            throw new CustomException("File size exceeds maximum allowed: " +
                    formatFileSize(fileSize) + " > " + formatFileSize(maxAllowedSize),
                    HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        String extension = getFileExtension(file.getOriginalFilename());

        contentType = contentType != null ? contentType.toLowerCase() : null;
        if (Objects.isNull(contentType) || !ALLOWED_IMAGE_TYPES.contains(contentType) || !ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new CustomException("Invalid file extension: " + extension, HttpStatus.BAD_REQUEST);
        }
        String fileName = "profile_" + user.getId() + "_" + UUID.randomUUID() + extension;
        String savedPath = saveFile(file, fileName);
        if (user.getProfilePicture() != null) {
            Files.deleteIfExists(Paths.get(user.getProfilePicture()));
        }
        user.setProfilePicture(savedPath);
        userRepository.save(user);

        return AppResponse.builder()
                .status(HttpStatus.OK.toString())
                .message("Image uploaded successfully")
                .data(Map.of(
                        "fileName", fileName,
                        "path", savedPath
                ))
                .error("")
                .build();
    }

    private String saveFile(MultipartFile file, String fileName) {
        try {
            Path uploadDirectory = Paths.get("uploads", "images");
            Files.createDirectories(uploadDirectory);
            Path filePath = uploadDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();

        } catch (IOException e) {
            throw new CustomException(
                    "Failed to save file",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024)); //
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
