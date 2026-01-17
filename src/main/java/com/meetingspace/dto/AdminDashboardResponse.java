package com.meetingspace.dto;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardResponse {

    private LocalDate date;
    private long totalRooms;
    private long totalBookingsToday;
    private List<AdminRoomSummaryDto> rooms;
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public long getTotalRooms() {
		return totalRooms;
	}
	public void setTotalRooms(long totalRooms) {
		this.totalRooms = totalRooms;
	}
	public long getTotalBookingsToday() {
		return totalBookingsToday;
	}
	public void setTotalBookingsToday(long totalBookingsToday) {
		this.totalBookingsToday = totalBookingsToday;
	}
	public List<AdminRoomSummaryDto> getRooms() {
		return rooms;
	}
	public void setRooms(List<AdminRoomSummaryDto> rooms) {
		this.rooms = rooms;
	}
    
}
