//package com.spotroute.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UncheckedIOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.UUID;
//
//    /** Dev/simple implementation. Swap for an S3/GCS implementation (private bucket) in production. */
//    @Service
//    public class LocalFileStorageService implements FileStorageService {
//
//        private final Path root;
//
//        public LocalFileStorageService(@Value("${driver.storage.root}") String root) {
//            this.root = Path.of(root).toAbsolutePath().normalize();
//        }
//
//        @Override
//        public String store(String directory, MultipartFile file) {
//            String key = directory + "/" + UUID.randomUUID() + "." + extension(file.getContentType());
//            Path target = resolve(key);
//            try {
//                Files.createDirectories(target.getParent());
//                try (InputStream in = file.getInputStream()) {
//                    Files.copy(in, target);
//                }
//            } catch (IOException e) {
//                throw new UncheckedIOException("Failed to store file " + key, e);
//            }
//            return key;
//        }
//
//        @Override
//        public void delete(String key) {
//            try {
//                Files.deleteIfExists(resolve(key));
//            } catch (IOException e) {
//                throw new UncheckedIOException("Failed to delete file " + key, e);
//            }
//        }
//
//        private Path resolve(String key) {
//            Path p = root.resolve(key).normalize();
//            if (!p.startsWith(root)) {
//                throw new IllegalStateException("Invalid storage key: " + key);
//            }
//            return p;
//        }
//
//        /** Extension comes from the validated content type, never from the client's filename. */
//        private static String extension(String contentType) {
//            String ct = contentType == null ? "" : contentType.split(";")[0].trim().toLowerCase();
//            return switch (ct) {
//                case "image/jpeg" -> "jpg";
//                case "image/png" -> "png";
//                case "application/pdf" -> "pdf";
//                default -> {
//                    String sub = ct.startsWith("video/") ? ct.substring(6) : "";
//                    yield sub.matches("[a-z0-9]{1,10}") ? sub : "bin";
//                }
//            };
//        }
//    }
//}
