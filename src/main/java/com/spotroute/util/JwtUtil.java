package com.spotroute.util;

import com.spotroute.Properties.JwtProperties;
import com.spotroute.persistence.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    //Use for Production
//    For production:
//
//    Generate a random 256-bit key.
//    Store it Base64 encoded.
//    Decode it before creating the signing key.

//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
//    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate an access token for authentication.
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        Map<String, Object> claims = this.generateClaims(user);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtProperties.getExpiration())))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a refresh token with a longer expiration time.
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Map<String, Object> claims = this.generateClaims(user);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtProperties.getExpiration())))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extract username (email) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract any claim from the token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    /**
     * Validate the provided token against the given username.
     */
    public boolean isValid(String token, UserDetails userDetails) {
        return userDetails.equals(extractUsername(token)) && !isExpired(token);
//        try {
//            String email = extractEmail(token);
//            return email.equals(userDetails.getUsername()) && !isExpired(token);
//        } catch (JwtException | IllegalArgumentException e) {
//            log.warn("Invalid JWT: {}", e.getMessage());
//            return false;
//        }
    }

    private boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    private  Map<String, Object> generateClaims(User user) {
        Map<String, Object> otherInfo = new LinkedHashMap<>();
        otherInfo.put("id", user.getId());
        otherInfo.put("firstName", user.getFirstName());
        otherInfo.put("lastName", user.getLastName());
        otherInfo.put("email", user.getEmail());
        otherInfo.put("role", user.getRole());


        return otherInfo;
    }
}
