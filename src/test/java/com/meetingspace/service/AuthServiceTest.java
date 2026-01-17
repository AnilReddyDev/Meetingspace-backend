
package com.meetingspace.service;

import com.meetingspace.entity.Role;
import com.meetingspace.entity.User;
import com.meetingspace.entity.VerificationToken;
import com.meetingspace.repository.RoleRepository;
import com.meetingspace.repository.UserRepository;
import com.meetingspace.repository.VerificationTokenRepository;
import com.meetingspace.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private VerificationTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        tokenRepository = Mockito.mock(VerificationTokenRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtUtil = new JwtUtil("meetingspace-secret-key-123456789012", 3600000);

        authService = new AuthService(userRepository, roleRepository, tokenRepository, passwordEncoder, jwtUtil);

        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    }

    @Test
    void register_success() {
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.empty());
        Role role = new Role();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        authService.register("uday","uday@hcltech.com","Strong@123!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("uday@hcltech.com", userCaptor.getValue().getEmail());
        assertEquals("hashed", userCaptor.getValue().getPasswordHash());
        verify(tokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void register_failsOnExistingEmail() {
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.of(new User()));
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.register("u","uday@hcltech.com","Strong@123!")
        );
        assertTrue(ex.getMessage().contains("Email already registered"));
    }

    @Test
    void register_failsOnWeakPassword() {
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.register("u","uday@hcltech.com","weak")
        );
        assertTrue(ex.getMessage().contains("Password must be at least 8 characters long"));
    }

    @Test
    void verifyEmail_success() {
        VerificationToken vt = new VerificationToken();
        User user = new User();
        vt.setUser(user);
        vt.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(vt));

        authService.verifyEmail(UUID.randomUUID().toString());

        assertTrue(user.isVerified());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(vt);
    }

    @Test
    void verifyEmail_expiredToken() {
        VerificationToken vt = new VerificationToken();
        vt.setUser(new User());
        vt.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(vt));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.verifyEmail("any")
        );
        assertTrue(ex.getMessage().contains("Token expired"));
    }

    @Test
    void login_success() {
        User user = new User();
        user.setEmail("uday@hcltech.com");
        user.setVerified(true);
        user.setPasswordHash("hashed");
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Strong@123!", "hashed")).thenReturn(true);

        String token = authService.login("uday@hcltech.com","Strong@123!");
        assertNotNull(token);
    }

    @Test
    void login_failsUserNotFound() {
        when(userRepository.findByEmail("no@hcltech.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("no@hcltech.com","x")
        );
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void login_failsUnverified() {
        User user = new User();
        user.setEmail("uday@hcltech.com");
        user.setVerified(false);
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.of(user));
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("uday@hcltech.com","x")
        );
        assertTrue(ex.getMessage().contains("Email not verified"));
    }

    @Test
    void login_failsInvalidPassword() {
        User user = new User();
        user.setEmail("uday@hcltech.com");
        user.setVerified(true);
        user.setPasswordHash("hashed");
        when(userRepository.findByEmail("uday@hcltech.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("uday@hcltech.com","bad")
        );
        assertTrue(ex.getMessage().contains("Invalid password"));
    }
}
