package com.meetingspace.service;

import com.meetingspace.entity.*;
import com.meetingspace.repository.*;
import com.meetingspace.security.JwtUtil;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final VerificationTokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;




    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       VerificationTokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // REGISTER
    public void register(String username, String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }

        String passwordRegex =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

        if (!password.matches(passwordRegex)) {
            throw new RuntimeException(
                    "Password must be at least 8 characters long and contain " +
                            "one uppercase letter, one lowercase letter, one number, " +
                            "and one special character"
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // Encode AFTER validation
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

        System.out.println("Verification Token (for testing): " + rawToken);
    }



    // VERIFY EMAIL
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

    // LOGIN
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
