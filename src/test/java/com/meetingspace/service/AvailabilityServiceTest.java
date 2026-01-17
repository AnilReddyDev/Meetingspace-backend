
package com.meetingspace.service;

import com.meetingspace.dto.AvailabilityResponse;
import com.meetingspace.entity.Booking;
import com.meetingspace.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityServiceTest {

    @Test
    void getAvailability_shouldBuildAvailableAndBookedSlots() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

        Booking b1 = new Booking();
        b1.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        b1.setEndTime(LocalDateTime.of(2025,1,1,11,0));
        b1.setStatus("CONFIRMED");

        Booking b2 = new Booking();
        b2.setStartTime(LocalDateTime.of(2025,1,1,12,30));
        b2.setEndTime(LocalDateTime.of(2025,1,1,13,0));
        b2.setStatus("CONFIRMED");

        Mockito.when(bookingRepository.findBookingsForDate(7L, LocalDate.of(2025,1,1)))
            .thenReturn(List.of(b1, b2));

        AvailabilityService service = new AvailabilityService(bookingRepository);
        AvailabilityResponse resp = service.getAvailability(7L, LocalDate.of(2025,1,1));

        assertNotNull(resp.getAvailability());
        assertEquals(5, resp.getAvailability().size());
        assertEquals("AVAILABLE", resp.getAvailability().get(0).getStatus()); // 9-10
        assertEquals("BOOKED", resp.getAvailability().get(1).getStatus());    // 10-11
        assertEquals("AVAILABLE", resp.getAvailability().get(2).getStatus()); // 11-12:30
        assertEquals("BOOKED", resp.getAvailability().get(3).getStatus());    // 12:30-13
        assertEquals("AVAILABLE", resp.getAvailability().get(4).getStatus()); // 13-18
    }

    @Test
    void getAvailability_noBookings_returnsFullDayAvailable() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);
        Mockito.when(repo.findBookingsForDate(1L, LocalDate.of(2025,1,1)))
            .thenReturn(List.of());
        AvailabilityService service = new AvailabilityService(repo);
        AvailabilityResponse resp = service.getAvailability(1L, LocalDate.of(2025,1,1));
        assertEquals(1, resp.getAvailability().size());
        assertEquals("AVAILABLE", resp.getAvailability().get(0).getStatus());
        assertTrue(resp.getAvailability().get(0).getStart().endsWith("09:00"));
        assertTrue(resp.getAvailability().get(0).getEnd().endsWith("18:00"));
    }
}
