package com.meetingspace.controller;

import com.meetingspace.dto.LoginRequest;
import com.meetingspace.dto.RegisterRequest;
import com.meetingspace.service.AuthService;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {


    private final AuthService authService;


    public AuthController(AuthService authService) {
        super();
        this.authService = authService;
    }

    // REGISTER
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest request) {
        authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        return Map.of("message", "Registration successful. Verify email.");
    }

    // VERIFY EMAIL
    @GetMapping("/verify")
    public Map<String, String> verify(@RequestParam String token) {
        authService.verifyEmail(token);
        return Map.of("message", "Email verified successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        String jwt = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return Map.of("accessToken", jwt);
    }
}
