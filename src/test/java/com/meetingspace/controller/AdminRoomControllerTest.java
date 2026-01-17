
package com.meetingspace.controller;

import com.meetingspace.dto.RoomRequest;
import com.meetingspace.entity.Room;
import com.meetingspace.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminRoomController.class)
@Import(AdminRoomControllerTest.MethodSecurityTestConfig.class)
class AdminRoomControllerTest {

    // Enable @PreAuthorize("hasRole('ADMIN')") in the test slice
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    // Mock JwtFilter constructor dependencies so the real filter can be built
    @MockitoBean
    private com.meetingspace.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.meetingspace.repository.UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoom_shouldReturnCreatedRoom() throws Exception {
        Room created = new Room();
        created.setCapacity(10);
        created.setFloor(2);
        created.setStatus("ACTIVE");
        created.setName("Innovation Lab");

        Mockito.when(roomService.create(Mockito.any())).thenReturn(created);

        String payload = """
            {
              "name": "Innovation Lab",
              "capacity": 10,
              "floor": 2,
              "amenityIds": [1,2]
            }
            """;

        mockMvc.perform(post("/api/v1/admin/rooms")
                .with(csrf()) // ✅ include CSRF for POST
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Innovation Lab"))
            .andExpect(jsonPath("$.capacity").value(10));

        ArgumentCaptor<RoomRequest> captor = ArgumentCaptor.forClass(RoomRequest.class);
        verify(roomService).create(captor.capture());
    }

    // Unauthenticated -> expect 401 Unauthorized (add CSRF to avoid 403)
    @Test
    void createRoom_requiresAuthentication() throws Exception {
        String payload = """
            {"name":"A","capacity":3,"floor":1,"amenityIds":[3]}
            """;
        mockMvc.perform(post("/api/v1/admin/rooms")
                .with(csrf()) // ✅ CSRF so authentication entry point handles it
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized());
    }

    // Authenticated but not ADMIN -> 403 Forbidden
    @Test
    @WithMockUser(roles = "USER")
    void createRoom_requiresAdminRole() throws Exception {
        String payload = """
            {"name":"A","capacity":3,"floor":1,"amenityIds":[3]}
            """;
        mockMvc.perform(post("/api/v1/admin/rooms")
                .with(csrf()) // ✅ CSRF for POST
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRooms_shouldReturnList() throws Exception {
        Room r1 = new Room();
        r1.setName("A");
        r1.setCapacity(4);
        Room r2 = new Room();
        r2.setName("B");
        r2.setCapacity(8);
        Mockito.when(roomService.getAll()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/admin/rooms"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("A"))
            .andExpect(jsonPath("$[1].capacity").value(8));
    }
}
