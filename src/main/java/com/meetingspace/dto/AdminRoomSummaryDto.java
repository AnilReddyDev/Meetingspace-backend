package com.meetingspace.dto;

public class AdminRoomSummaryDto {

    public Long roomId;
    public String name;
    public int capacity;

    public long confirmedBookings;
    public long cancelledBookings;
    public long totalBookings;

    public int utilizationPercentage;
}

