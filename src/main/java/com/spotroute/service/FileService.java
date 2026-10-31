package com.spotroute.service;


import com.spotroute.core.enums.DocumentType;
import com.spotroute.core.enums.Status;
import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.response.AppResponse;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.User;
import com.spotroute.repository.UserRepository;
import com.spotroute.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;
    private static final long MAX_DRIVER_DOCUMENT_BYTES = 15L * 1024 * 1024; // 15 MB
    private static final Set<String> DRIVER_DOCUMENT_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");
    private static final Set<String> DRIVER_DOCUMENT_EXTENSIONS = Set.of("jpg", "jpeg", "png", "pdf");
    private static final Set<String> DRIVER_VIDEO_EXTENSIONS = Set.of("mp4", "mov", "m4v", "webm");
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
        String encodedImage = Base64.getEncoder().encodeToString(file.getBytes());
        user.setProfilePicture(encodedImage);
        userRepository.save(user);

        return AppResponse.builder()
                .status(HttpStatus.OK.toString())
                .message("Profile picture stored successfully")
                .data(null)
                .error("")
                .build();
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


    /** Size cap standing in for the 5 minute limit (duration can't be read from the upload alone). */
//    @Value("${driver.max-video-bytes:104857600}")
    public AppResponse<Object> validateDriverDocument(DocumentType type, MultipartFile file) {
//        DriverProfile driverProfile = SecurityUtil.getLoggedInDriverFromContext();
        if (type == null) {
            throw new CustomException("Document type is required", HttpStatus.BAD_REQUEST
            );
        }
        if (file == null || file.isEmpty()) {
            throw new CustomException(type + " file is required", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType() == null ? ""
                : file.getContentType().split(";")[0].trim().toLowerCase();
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();

        long size = file.getSize();
        long maxDriverVideoBytes = 104857600;
        if (type.isVideo()) {
            if (!contentType.startsWith("video/") || !DRIVER_VIDEO_EXTENSIONS.contains(extension)) {
                throw new CustomException(type + " must be a video (mp4, mov, m4v or webm)", HttpStatus.BAD_REQUEST);
            }
            if (size > maxDriverVideoBytes) {
                throw new CustomException(type + " exceeds maximum allowed: " + formatFileSize(size)
                        + " > " + formatFileSize(maxDriverVideoBytes), HttpStatus.BAD_REQUEST);
            }
            return AppResponse.builder()
                    .status(HttpStatus.OK.toString())
                    .message("Video validated successfully")
                    .data(null)
                    .error("")
                    .build();
        }

        if (!DRIVER_DOCUMENT_TYPES.contains(contentType) || !DRIVER_DOCUMENT_EXTENSIONS.contains(extension)) {
            throw new CustomException(type + " must be a JPEG, PNG or PDF file", HttpStatus.BAD_REQUEST);
        }
        if (size > MAX_DRIVER_DOCUMENT_BYTES) {
            throw new CustomException(type + " exceeds maximum allowed: " + formatFileSize(size)
                    + " > " + formatFileSize(MAX_DRIVER_DOCUMENT_BYTES), HttpStatus.BAD_REQUEST);
        }
        if (!hasValidSignature(file, contentType)) {
            throw new CustomException(type + " content does not match its file type", HttpStatus.BAD_REQUEST);
        }

        return AppResponse.builder()
                .status(HttpStatus.OK.toString())
                .message("Document validated successfully")
                .data(null)
                .error("")
                .build();
    }


    /** Returns the saved path (this is what goes into DriverDocument.fileUrl). */
    public String storeDriverDocument(String userId, DocumentType type, MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        String fileName = type.name().toLowerCase() + "_" + UUID.randomUUID() + "." + extension;
        Path directory = Paths.get("uploads", "driver-documents", String.valueOf(userId));
        try {
            Files.createDirectories(directory);
            Path filePath = directory.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, filePath);
            }
            return filePath.toString();
        } catch (IOException e) {
            throw new CustomException("Failed to save file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** A failed delete only leaves an orphan file, so it is logged rather than thrown. */
    public void deleteQuietly(String path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            log.error("Could not delete file {}", path, e);
        }
    }

        private boolean hasValidSignature(MultipartFile file, String contentType) {
            try {
                byte[] header = file.getInputStream().readNBytes(8);
                if (contentType.equals("image/jpeg")) {
                    return header.length >= 3
                            && (header[0] & 0xFF) == 0xFF
                            && (header[1] & 0xFF) == 0xD8
                            && (header[2] & 0xFF) == 0xFF;
                }
                if (contentType.equals("image/png")) {
                    return header.length >= 8
                            && (header[0] & 0xFF) == 0x89
                            && header[1] == 0x50
                            && header[2] == 0x4E
                            && header[3] == 0x47
                            && header[4] == 0x0D
                            && header[5] == 0x0A
                            && header[6] == 0x1A
                            && header[7] == 0x0A;
                }
                if (contentType.equals("application/pdf")) {
                    return header.length >= 5
                            && header[0] == 0x25
                            && header[1] == 0x50
                            && header[2] == 0x44
                            && header[3] == 0x46
                            && header[4] == 0x2D;
                }
                return false;
            } catch (IOException e) {
                throw new CustomException(
                        "Failed to read uploaded file",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

}
