
package com.meetingspace.service;

import com.meetingspace.dto.AdminRoomsDashboardResponse;
import com.meetingspace.entity.Room;
import com.meetingspace.repository.BookingRepository;
import com.meetingspace.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class AdminDashboardServiceTest {

    @Test
    void getRoomsDashboardForDate_shouldAggregateCountsAndUtilization() {
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

        // Create rooms and set basic attributes
        Room r1 = new Room();
        r1.setName("A");
        r1.setCapacity(10);

        Room r2 = new Room();
        r2.setName("B");
        r2.setCapacity(0);

        // ✅ Set private 'id' via reflection because Room has no public setId(...)
        setId(r1, 1L);
        setId(r2, 2L);

        when(roomRepository.findAll()).thenReturn(List.of(r1, r2));

        LocalDate date = LocalDate.of(2026, 1, 16);

        // ✅ Stub per-room counts explicitly using the new repository method
        when(bookingRepository.countByRoomIdAndStatusAndOverlap(
                eq(1L), eq("CONFIRMED"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(3);
        when(bookingRepository.countByRoomIdAndStatusAndOverlap(
                eq(2L), eq("CONFIRMED"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(0);

        when(bookingRepository.countByRoomIdAndStatusAndOverlap(
                eq(1L), eq("CANCELLED"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(1);
        when(bookingRepository.countByRoomIdAndStatusAndOverlap(
                eq(2L), eq("CANCELLED"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(2);

        AdminDashboardService service = new AdminDashboardService(roomRepository, bookingRepository);
        AdminRoomsDashboardResponse resp = service.getRoomsDashboardForDate(date);

        // Assertions
        assertEquals(date.toString(), resp.getDate());
        assertEquals(2, resp.getTotalRooms());
        assertEquals(6, resp.getTotalBookingsToday()); // (3+1) + (0+2)
        assertNotNull(resp.getRooms());
        assertEquals(2, resp.getRooms().size());

        // Stable lookups by ID
        AdminRoomsDashboardResponse.RoomSummary room1 = resp.getRooms().stream()
                .filter(rs -> rs.roomId == 1L).findFirst().orElseThrow();
        AdminRoomsDashboardResponse.RoomSummary room2 = resp.getRooms().stream()
                .filter(rs -> rs.roomId == 2L).findFirst().orElseThrow();

        // Utilization: possibleUnits=9 -> round(confirmed * 100 / 9)
        assertEquals(33, room1.utilizationPercentage); // round(3/9*100) = 33
        assertEquals(0, room2.utilizationPercentage);  // capacity 0 & confirmed 0

        // Weighted average utilization by capacity: (33*10 + 0*0) / (10+0) = 33.0
        assertEquals(33.0, resp.getAvgUtilization());
    }

    /**
     * Utility to set private field 'id' on Room since there's no public setter.
     */
    private static void setId(Room room, long id) {
        try {
            Field f = Room.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(room, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set Room.id via reflection", e);
        }
    }
}
