
package com.meetingspace.controller; 
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.meetingspace.dto.AdminRoomsDashboardResponse;
import com.meetingspace.service.AdminDashboardService;
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

import java.time.LocalDate;

@WebMvcTest(AdminDashboardController.class)
@Import(AdminDashboardControllerTest.MethodSecurityTestConfig.class)
class AdminDashboardControllerTest {

    // ✅ Enable @PreAuthorize("hasRole('ADMIN')") in the @WebMvcTest slice
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {}

    @Autowired
    private MockMvc mockMvc;

    // Controller dependency
    @MockitoBean
    private AdminDashboardService adminDashboardService;

    // ✅ Mock JwtFilter constructor dependencies so the real filter can be built
    @MockitoBean
    private com.meetingspace.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.meetingspace.repository.UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoomsDashboard_shouldReturnDashboardForDate() throws Exception {
        LocalDate date = LocalDate.of(2026, 1, 16);

        AdminRoomsDashboardResponse.RoomSummary rs = new AdminRoomsDashboardResponse.RoomSummary();
        rs.roomId = 1L;
        rs.name = "Room A";
        rs.capacity = 10;
        rs.confirmedBookings = 2;
        rs.cancelledBookings = 1;
        rs.totalBookings = 3;
        rs.utilizationPercentage = 22; // round(2/9*100) = 22

        AdminRoomsDashboardResponse response = new AdminRoomsDashboardResponse();
        response.setDate(date.toString());
        response.setTotalRooms(1);
        response.setTotalBookingsToday(3);
        response.setAvgUtilization(22.0); // capacity-weighted (10 capacity)
        response.setRooms(List.of(rs));

        Mockito.when(adminDashboardService.getRoomsDashboardForDate(date)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/dashboard/rooms")
                        .param("date", date.toString())
                        .accept(APPLICATION_JSON))
               .andExpect(status().isOk())
               // We don't assert content-type because controller returns a POJO without produces
               .andExpect(jsonPath("$.date").value(date.toString()))
               .andExpect(jsonPath("$.totalRooms").value(1))
               .andExpect(jsonPath("$.totalBookingsToday").value(3))
               .andExpect(jsonPath("$.avgUtilization").value(22.0))
               .andExpect(jsonPath("$.rooms[0].name").value("Room A"))
               .andExpect(jsonPath("$.rooms[0].utilizationPercentage").value(22));
    }

    @Test
    void getRoomsDashboard_requiresAuthentication() throws Exception {
        // Unauthenticated -> 401 due to class-level @PreAuthorize
        mockMvc.perform(get("/api/v1/admin/dashboard/rooms")
                        .param("date", "2026-01-16")
                        .accept(APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRoomsDashboard_requiresAdminRole() throws Exception {
        // Authenticated but not ADMIN -> 403 Forbidden
        mockMvc.perform(get("/api/v1/admin/dashboard/rooms")
                        .param("date", "2026-01-16")
                        .accept(APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }
}



