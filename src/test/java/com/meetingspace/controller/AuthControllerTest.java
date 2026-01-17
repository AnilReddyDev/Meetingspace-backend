
package com.meetingspace.controller;

import com.meetingspace.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.MethodSecurityTestConfig.class) // enables @PreAuthorize if used in AuthController (not needed here, safe to include)
class AuthControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // ✅ Mock JwtFilter constructor dependencies so the real filter can be constructed in the slice
    @MockitoBean
    private com.meetingspace.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.meetingspace.repository.UserRepository userRepository;

    @Test
    @WithMockUser // authenticated user to avoid 401
    void register_shouldReturnMessage() throws Exception {
        String payload = """
        {"username":"uday","email":"uday@hcltech.com","password":"Strong@123"}
        """;

        // No return needed; controller just calls service and returns message
        Mockito.doNothing().when(authService).register("uday", "uday@hcltech.com", "Strong@123");

        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf()) // ✅ CSRF for POST
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Registration successful. Verify email."));

        Mockito.verify(authService).register("uday", "uday@hcltech.com", "Strong@123");
    }

    @Test
    @WithMockUser // authenticated user to avoid 401
    void verify_shouldReturnMessage() throws Exception {
        Mockito.doNothing().when(authService).verifyEmail("abc");

        mockMvc.perform(get("/api/v1/auth/verify").param("token", "abc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Email verified successfully"));

        Mockito.verify(authService).verifyEmail("abc");
    }

    @Test
    @WithMockUser // authenticated user to avoid 401
    void login_shouldReturnAccessToken() throws Exception {
        Mockito.when(authService.login("uday@hcltech.com", "Strong@123"))
               .thenReturn("jwt-token");

        String payload = """
        {"email":"uday@hcltech.com","password":"Strong@123"}
        """;

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf()) // ✅ CSRF for POST
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }
}
