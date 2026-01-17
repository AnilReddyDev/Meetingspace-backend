
package com.meetingspace.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DtosTest {

    @Test
    void bookingRequest_fields() {
        BookingRequest br = new BookingRequest();
        br.setRoomId(7L);
        br.setStartTime(java.time.LocalDateTime.now());
        br.setEndTime(br.getStartTime().plusHours(1));
        assertEquals(7L, br.getRoomId());
    }

    @Test
    void registerRequest_getters() {
        RegisterRequest rr = new RegisterRequest();
        // Private fields without setters; framework populates via Jackson in runtime.
        assertNull(rr.getUsername());
        assertNull(rr.getEmail());
    }

    @Test
    void adminDashboardResponse_fields() {
        AdminRoomSummaryDto dto = new AdminRoomSummaryDto();
        dto.setRoomId(1L);
        dto.setName("A");
        dto.setCapacity(10);
        dto.setConfirmedBookings(2);
        dto.setCancelledBookings(1);
        dto.setTotalBookings(3);
        dto.setUtilizationPercentage(20);

        AdminDashboardResponse resp = new AdminDashboardResponse();
        resp.setDate(java.time.LocalDate.now());
        resp.setTotalRooms(1);
        resp.setTotalBookingsToday(3);
        resp.setRooms(List.of(dto));

        assertEquals(1, resp.getTotalRooms());
        assertEquals(20, resp.getRooms().get(0).getUtilizationPercentage());
    }

    @Test
    void availabilityResponse_fields() {
        AvailabilityResponse.Slot s = new AvailabilityResponse.Slot();
        s.setStart("2025-01-01T09:00");
        s.setEnd("2025-01-01T10:00");
        s.setStatus("AVAILABLE");
        AvailabilityResponse r = new AvailabilityResponse();
        r.setAvailability(java.util.List.of(s));
        assertEquals("AVAILABLE", r.getAvailability().get(0).getStatus());
    }

    @Test
    void roomRequest_fields() {
        RoomRequest rr = new RoomRequest();
        rr.setName("Lab");
        rr.setCapacity(10);
        rr.setFloor(2);
        rr.setAmenityIds(java.util.List.of(1L, 2L));
        assertEquals("Lab", rr.getName());
        assertEquals(2, rr.getAmenityIds().size());
    }

    @Test
    void loginRequest_getters() {
        LoginRequest lr = new LoginRequest();
        assertNull(lr.getEmail());
        assertNull(lr.getPassword());
    }
}
