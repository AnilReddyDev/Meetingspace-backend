package com.meetingspace.controller;

import com.meetingspace.entity.Room;
import com.meetingspace.repository.RoomRepository;
import com.meetingspace.repository.BookingRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // DASHBOARD ROOM SUMMARY
    @GetMapping("/rooms")
    public Map<String, Object> getRoomSummary() {

        List<Room> rooms = roomRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        for (Room room : rooms) {
            long count = bookingRepository.countBookingsForRoomToday(
                    room.getId(),
                    startOfDay,
                    endOfDay
            );

            response.add(Map.of(
                    "roomId", room.getId(),
                    "name", room.getName(),
                    "capacity", room.getCapacity(),
                    "todayBookings", count
            ));
        }

        return Map.of("rooms", response);
    }
}
