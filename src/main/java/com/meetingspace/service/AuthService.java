package com.meetingspace.service;

import com.meetingspace.entity.*;
import com.meetingspace.repository.*;
import com.meetingspace.security.JwtUtil;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ================= REGISTER =================
    public void register(String username, String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        user.getRoles().add(role);
        userRepository.save(user);

        // Create verification token
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = DigestUtils.sha256Hex(rawToken);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setTokenHash(hashedToken);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));

        tokenRepository.save(token);

        // In real project → send email
        System.out.println("Verification Token (for testing): " + rawToken);
    }

    // ================= VERIFY EMAIL =================
    public void verifyEmail(String token) {

        String hashedToken = DigestUtils.sha256Hex(token);

        VerificationToken verificationToken = tokenRepository
                .findByTokenHash(hashedToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);

        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    // ================= LOGIN =================
    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        if (!user.isActive()) {
            throw new RuntimeException("User is inactive");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user);
    }
}
