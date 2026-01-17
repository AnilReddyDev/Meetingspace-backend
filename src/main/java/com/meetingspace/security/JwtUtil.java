package com.meetingspace.security;

import com.meetingspace.entity.User;
import com.meetingspace.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private final Key key;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // GENERATE TOKEN
    public String generateToken(User user) {


        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList() 
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // EXTRACT EMAIL
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // EXTRACT ROLES
    public List<String> extractRoles(String token) {
        return parseClaims(token).get("roles", List.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
