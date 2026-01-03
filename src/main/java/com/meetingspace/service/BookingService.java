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

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;


    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        super();
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    // CREATE BOOKING
    @Transactional
    public Booking book(Long roomId,
                        LocalDateTime startTime,
                        LocalDateTime endTime,
                        User user) {

        if (!startTime.isBefore(endTime)) {
            throw new RuntimeException("Start time must be before end time");
        }

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


    // CANCEL BOOKING (OPTIONAL – backward compatibility)
    @Transactional
    public void cancel(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    //CANCEL BY SLOT
    @Transactional
    public void cancelBySlot(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            User user
    ) {

        Booking booking = bookingRepository
                .findForCancellationByUser(
                        roomId,
                        startTime,
                        endTime,
                        user.getEmail()
                )
                .orElseThrow(() -> new RuntimeException(
                        "No active booking found for this user and time slot"
                ));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }



}
