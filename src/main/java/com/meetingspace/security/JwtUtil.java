package com.meetingspace.security;

import com.meetingspace.entity.User;
import com.meetingspace.entity.Role;
import io.jsonwebtoken.*;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long expiration;

    // GENERATE TOKEN
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // EXTRACT EMAIL
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // EXTRACT ROLES
    public List<String> extractRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    // VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
