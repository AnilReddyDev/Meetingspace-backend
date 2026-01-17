
package com.meetingspace.controller;

import com.meetingspace.dto.AvailabilityResponse;
import com.meetingspace.service.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    // ✅ Allow real JwtFilter to be constructed
    @MockitoBean
    private com.meetingspace.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.meetingspace.repository.UserRepository userRepository;

    @Test
    @WithMockUser // ✅ authenticated user → 200 OK
    void getAvailability_shouldReturnSlots() throws Exception {
        AvailabilityResponse.Slot s1 = new AvailabilityResponse.Slot();
        s1.setStart("2025-01-01T09:00");
        s1.setEnd("2025-01-01T10:00");
        s1.setStatus("AVAILABLE");

        AvailabilityResponse.Slot s2 = new AvailabilityResponse.Slot();
        s2.setStart("2025-01-01T10:00");
        s2.setEnd("2025-01-01T11:00");
        s2.setStatus("BOOKED");

        AvailabilityResponse resp = new AvailabilityResponse();
        resp.setAvailability(List.of(s1, s2));

        Mockito.when(availabilityService.getAvailability(1L,
                java.time.LocalDate.parse("2025-01-01")))
            .thenReturn(resp);

        mockMvc.perform(get("/api/v1/rooms/{roomId}", 1L)
                .param("date", "2025-01-01")
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availability[0].status").value("AVAILABLE"))
            .andExpect(jsonPath("$.availability[1].status").value("BOOKED"));
    }
}
