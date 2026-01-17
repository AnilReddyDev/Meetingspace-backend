package com.meetingspace.service;

import com.meetingspace.dto.AdminRoomsDashboardResponse;
import com.meetingspace.entity.Booking;
import com.meetingspace.entity.Room;
import com.meetingspace.repository.BookingRepository;
import com.meetingspace.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;

    // Use your business timezone
    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    public AdminDashboardService(RoomRepository roomRepo, BookingRepository bookingRepo) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
    }

    public AdminRoomsDashboardResponse getRoomsDashboardForDate(LocalDate date) {
        // Build local day window [start, end)
        LocalDateTime startOfDay = date.atStartOfDay();                      // 00:00
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();            // next day 00:00

        // Load all rooms
        List<Room> rooms = roomRepo.findAll();

        List<AdminRoomsDashboardResponse.RoomSummary> roomSummaries = new ArrayList<>();
        int totalBookingsForDay = 0;

        // Optional: track weighted utilization
        long capacitySum = 0;
        double utilWeightedNumerator = 0.0;

        for (Room room : rooms) {
            // Count confirmed & cancelled bookings that overlap the day window
            int confirmed = bookingRepo.countByRoomIdAndStatusAndOverlap(
                    room.getId(), "CONFIRMED", startOfDay, endOfDay);
            int cancelled = bookingRepo.countByRoomIdAndStatusAndOverlap(
                    room.getId(), "CANCELLED", startOfDay, endOfDay);

            int totalForRoom = confirmed + cancelled;
            totalBookingsForDay += totalForRoom;

            // Compute utilization for this room:
            // Example heuristic: confirmed bookings count / max possible slots * 100.
            // If your slots are fixed 9–18 (9 hours), you can scale by hours instead.
            int utilizationPct = 0;
            int possibleUnits = 9; // if your business hours are 9–18 (9 one-hour slots)
            if (possibleUnits > 0) {
                utilizationPct = (int) Math.round((confirmed * 100.0) / possibleUnits);
            }

            AdminRoomsDashboardResponse.RoomSummary rs = new AdminRoomsDashboardResponse.RoomSummary();
            rs.roomId = room.getId();
            rs.name = room.getName();
            rs.capacity = room.getCapacity() == 0 ? 0 : room.getCapacity();
            rs.confirmedBookings = confirmed;
            rs.cancelledBookings = cancelled;
            rs.totalBookings = totalForRoom;
            rs.utilizationPercentage = utilizationPct;

            roomSummaries.add(rs);

            // For weighted avg util: weight by capacity
            capacitySum += rs.capacity;
            utilWeightedNumerator += (rs.utilizationPercentage * rs.capacity);
        }

        AdminRoomsDashboardResponse resp = new AdminRoomsDashboardResponse();
        resp.setDate(date.toString());
        resp.setTotalRooms(rooms.size());
        resp.setTotalBookingsToday(totalBookingsForDay);

        // Optional: provide avgUtilization top-level
        if (capacitySum > 0) {
            double avgUtil = utilWeightedNumerator / capacitySum;
            resp.setAvgUtilization(Math.round(avgUtil * 10.0) / 10.0);
        }

        resp.setRooms(roomSummaries);
        return resp;
    }
}
