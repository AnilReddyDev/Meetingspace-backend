package com.meetingspace.service;

import com.meetingspace.entity.Room;
import com.meetingspace.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminDashboardService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Map<String, Object>> roomSummaryToday() {

        List<Map<String, Object>> result = new ArrayList<>();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        for (Room room : roomRepository.findAll()) {

            long count = bookingRepository
                    .countBookingsForRoomToday(
                            room.getId(),
                            startOfDay,
                            endOfDay
                    );

            result.add(Map.of(
                    "roomId", room.getId(),
                    "name", room.getName(),
                    "capacity", room.getCapacity(),
                    "todayBookings", count
            ));
        }

        return result;
    }
}
