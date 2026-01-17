
package com.meetingspace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    public Long getId() { return id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getStartTime(){ return startTime; }
    public LocalDateTime getEndTime(){ return endTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public void setUser(User user) { this.user = user; }

    // Expose only IDs to the frontend
    public Long getRoomId() { return room != null ? room.getId() : null; }
    public Long getUserId() { return user != null ? user.getId() : null; }
}
