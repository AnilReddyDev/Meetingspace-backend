package com.meetingspace.dto;

public class AdminRoomSummaryDto {

    private Long roomId;
    private String name;
    private int capacity;

    private long confirmedBookings;
    private long cancelledBookings;
    private long totalBookings;

    private int utilizationPercentage;

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public long getConfirmedBookings() {
		return confirmedBookings;
	}

	public void setConfirmedBookings(long confirmedBookings) {
		this.confirmedBookings = confirmedBookings;
	}

	public long getCancelledBookings() {
		return cancelledBookings;
	}

	public void setCancelledBookings(long cancelledBookings) {
		this.cancelledBookings = cancelledBookings;
	}

	public long getTotalBookings() {
		return totalBookings;
	}

	public void setTotalBookings(long totalBookings) {
		this.totalBookings = totalBookings;
	}

	public int getUtilizationPercentage() {
		return utilizationPercentage;
	}

	public void setUtilizationPercentage(int utilizationPercentage) {
		this.utilizationPercentage = utilizationPercentage;
	}
    
}

