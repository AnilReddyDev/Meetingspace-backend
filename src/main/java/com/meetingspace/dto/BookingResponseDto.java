package com.meetingspace.dto;

import java.time.LocalDateTime;
import com.meetingspace.entity.Booking;

public class BookingResponseDto {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long roomId;

    public BookingResponseDto(Booking booking) {
        this.id = booking.getId();
        this.startTime = booking.getStartTime();
        this.endTime = booking.getEndTime();
        this.status = booking.getStatus();
        this.roomId = booking.getRoom().getId();
    }

    // getters
    public Long getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public Long getRoomId() { return roomId; }
}

