
package com.meetingspace.service;

import com.meetingspace.dto.BookingResponseDto;
import com.meetingspace.entity.Booking;
import com.meetingspace.entity.Room;
import com.meetingspace.entity.User;
import com.meetingspace.repository.BookingRepository;
import com.meetingspace.repository.RoomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    private static final int MIN_HOUR = 9;
    private static final int MAX_HOUR = 18; // exclusive end

    private void validateBusinessHours(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new RuntimeException("Start time must be before end time");
        }
        // Same-day bookings only
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new RuntimeException("Booking must start and end on the same day");
        }
        // On-the-hour and within 09–18
        if (start.getMinute() != 0 || end.getMinute() != 0 || start.getSecond() != 0 || end.getSecond() != 0) {
            throw new RuntimeException("Bookings must start/end on the hour");
        }
        if (start.getHour() < MIN_HOUR || end.getHour() > MAX_HOUR) {
            throw new RuntimeException("Bookings must be within 09:00–18:00");
        }
    }
    
 // GET ALL BOOKINGS
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    // GET USER BOOKINGS (Your Schedule)
       public List<BookingResponseDto> getUserBookings(String email) {

           return bookingRepository
                   .findByUserEmailOrderByStartTimeAsc(email)
                   .stream()
                   .map(BookingResponseDto::new)
                   .toList();
       }

       
    @Transactional
    public Booking book(Long roomId, LocalDateTime startTime, LocalDateTime endTime, User user) {
        validateBusinessHours(startTime, endTime);

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

    @Transactional
    public void cancelBySlot(Long roomId, LocalDateTime startTime, LocalDateTime endTime, User user) {
        var booking = bookingRepository
                .findForCancellationByUser(roomId, startTime, endTime, user.getEmail())
                .orElseThrow(() -> new RuntimeException("No active booking found for this user and time slot"));
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
