
package com.meetingspace.security;

import com.meetingspace.controller.AdminRoomController;
import com.meetingspace.service.RoomService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminRoomController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true) // Apply Spring Security filter chain in slice test
class SecurityConfigWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Satisfy SecurityConfig dependency so the filter chain builds
    @MockBean
    private JwtFilter jwtFilter;

    // Satisfy controller dependency
    @MockBean
    private RoomService roomService;

    @BeforeEach
    void setUpJwtFilterPassThrough() throws Exception {
        // Ensure JwtFilter does NOT set any authentication and just passes the request down the chain
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    // Unauthenticated -> expect 403 due to class-level @PreAuthorize("hasRole('ADMIN')")
    @Test
    @WithAnonymousUser
    void adminEndpoints_requireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/admin/rooms"))
               .andExpect(status().isForbidden()); // 403 is correct with @PreAuthorize and anonymous user
    }

    // Authenticated but NOT admin -> expect 403
    @Test
    @WithMockUser(username = "user@hcltech.com", roles = {"USER"})
    void adminEndpoints_requireAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/rooms"))
               .andExpect(status().isForbidden());
    }

    // Admin user -> expect 200
    @Test
    @WithMockUser(username = "admin@hcltech.com", roles = {"ADMIN"})
    void adminEndpoints_withAdminUser_succeeds() throws Exception {
        Mockito.when(roomService.getAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/admin/rooms"))
               .andExpect(status().isOk());
    }
}
