package com.meetingspace.service;

import com.meetingspace.dto.AdminDashboardResponse;
import com.meetingspace.dto.AdminRoomSummaryDto;
import com.meetingspace.entity.Room;
import com.meetingspace.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminDashboardService {

    private final RoomRepository roomRepository;

    private final BookingRepository bookingRepository;



    public AdminDashboardService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        super();
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }


    public AdminDashboardResponse getTodayDashboard() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Room> rooms = roomRepository.findAll();
        List<AdminRoomSummaryDto> roomSummaries = new ArrayList<>();

        long totalBookingsToday = 0;

        for (Room room : rooms) {

            long confirmed = bookingRepository.countConfirmedBookings(
                    room.getId(), startOfDay, endOfDay
            );

            long cancelled = bookingRepository.countCancelledBookings(
                    room.getId(), startOfDay, endOfDay
            );

            long total = confirmed + cancelled;
            totalBookingsToday += total;

            AdminRoomSummaryDto dto = new AdminRoomSummaryDto();
            dto.roomId = room.getId();
            dto.name = room.getName();
            dto.capacity = room.getCapacity();
            dto.confirmedBookings = confirmed;
            dto.cancelledBookings = cancelled;
            dto.totalBookings = total;

            dto.utilizationPercentage =
                    room.getCapacity() == 0
                            ? 0
                            : (int) Math.min(100, (confirmed * 100) / room.getCapacity());

            roomSummaries.add(dto);
        }

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.date = today;
        response.totalRooms = rooms.size();
        response.totalBookingsToday = totalBookingsToday;
        response.rooms = roomSummaries;

        return response;
    }


}
