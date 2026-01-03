package com.meetingspace.dto;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardResponse {

    public LocalDate date;
    public long totalRooms;
    public long totalBookingsToday;
    public List<AdminRoomSummaryDto> rooms;
}
