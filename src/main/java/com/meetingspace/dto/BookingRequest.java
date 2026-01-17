
package com.meetingspace.dto;

import java.time.LocalDateTime;

public class BookingRequest {
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
