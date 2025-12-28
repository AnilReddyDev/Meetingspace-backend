package com.meetingspace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne
    private Room room;

    @ManyToOne
    private User user;

    public LocalDateTime getStartTime(){return startTime;}
    public LocalDateTime getEndTime(){return endTime;}


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
