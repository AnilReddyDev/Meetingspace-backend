
package com.meetingspace.controller;

import com.meetingspace.dto.BookingRequest;
import com.meetingspace.entity.Booking;
import com.meetingspace.entity.Room;
import com.meetingspace.entity.User;
import com.meetingspace.repository.UserRepository;
import com.meetingspace.security.JwtUtil;
import com.meetingspace.service.BookingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    // Satisfy JwtFilter dependency so the context loads
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "alice@hcltech.com")
    void createBooking_shouldReturnBooking() throws Exception {
        // Arrange: mock authenticated user lookup
        User user = new User();
        user.setEmail("alice@hcltech.com");
        Mockito.when(userRepository.findByEmail("alice@hcltech.com"))
                .thenReturn(Optional.of(user));

        // Arrange: stub bookingService response
        Booking booking = new Booking();
        Room room = new Room();
        room.setCapacity(8);
        booking.setRoom(room);
        booking.setStartTime(LocalDateTime.parse("2025-01-01T10:00:00"));
        booking.setEndTime(LocalDateTime.parse("2025-01-01T11:00:00"));
        booking.setStatus("CONFIRMED");

        Mockito.when(bookingService.book(
                Mockito.eq(7L),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(user)
        )).thenReturn(booking);

        String payload = """
         {"roomId":7,"startTime":"2025-01-01T10:00:00","endTime":"2025-01-01T11:00:00"}
        """;

        // Act + Assert (include CSRF)
        mockMvc.perform(post("/api/v1/bookings")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.startTime").value("2025-01-01T10:00:00"))
            .andExpect(jsonPath("$.endTime").value("2025-01-01T11:00:00"));

        // Verify the service interaction with exact arguments
        verify(bookingService).book(
                Mockito.eq(7L),
                Mockito.eq(LocalDateTime.parse("2025-01-01T10:00:00")),
                Mockito.eq(LocalDateTime.parse("2025-01-01T11:00:00")),
                Mockito.eq(user)
        );
    }

    @Test
    @WithMockUser(username = "alice@hcltech.com")
    void cancelBookingById_shouldReturnCancelled() throws Exception {
        // Act + Assert (include CSRF)
        mockMvc.perform(post("/api/v1/bookings/99/cancel").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Verify service call
        verify(bookingService).cancel(99L);
    }

    @Test
    @WithMockUser(username = "alice@hcltech.com")
    void cancelBySlot_shouldReturnCancelled() throws Exception {
        // Arrange: mock authenticated user
        User user = new User();
        user.setEmail("alice@hcltech.com");
        Mockito.when(userRepository.findByEmail("alice@hcltech.com"))
                .thenReturn(Optional.of(user));

        String payload = """
         {"roomId":7,"startTime":"2025-01-01T10:00:00","endTime":"2025-01-01T11:00:00"}
        """;

        // Act + Assert (include CSRF)
        mockMvc.perform(post("/api/v1/bookings/cancel")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Verify service interaction
        verify(bookingService).cancelBySlot(
                Mockito.eq(7L),
                Mockito.eq(LocalDateTime.parse("2025-01-01T10:00:00")),
                Mockito.eq(LocalDateTime.parse("2025-01-01T11:00:00")),
                Mockito.eq(user)
        );
    }
}
