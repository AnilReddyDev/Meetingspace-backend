package com.meetingspace.service;

import com.meetingspace.entity.*;
import com.meetingspace.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    // ================= CREATE BOOKING =================
    @Transactional
    public Booking book(Long roomId,
                        LocalDateTime startTime,
                        LocalDateTime endTime,
                        User user) {

        if (bookingRepository.conflict(roomId, startTime, endTime).isPresent()) {
            throw new RuntimeException("Room already booked for this time slot");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Booking booking = new Booking();
        booking.setReference("BK-" + UUID.randomUUID());
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // ================= CANCEL BOOKING =================
    @Transactional
    public void cancel(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
