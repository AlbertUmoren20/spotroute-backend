package com.spotroute.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class ReviewKeyGuard {

    private final byte[] expectedKey;

    public ReviewKeyGuard(@Value("${admin.review-key:}") String reviewKey) {
        if (reviewKey == null || reviewKey.isBlank()) {
            throw new IllegalStateException(
                    "admin.review-key must be set: the /internal review endpoints cannot start unprotected");
        }
        this.expectedKey = reviewKey.getBytes(StandardCharsets.UTF_8);
    }

    public void check(String providedKey) {
        if (providedKey == null
                || !MessageDigest.isEqual(expectedKey, providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid review key");
        }
    }
}