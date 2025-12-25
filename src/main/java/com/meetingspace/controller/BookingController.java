package com.meetingspace.controller;

import com.meetingspace.dto.BookingRequest;
import com.meetingspace.entity.Booking;
import com.meetingspace.entity.User;
import com.meetingspace.service.BookingService;
import com.meetingspace.repository.UserRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    // CREATE BOOKING
    @PostMapping
    public Booking createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return bookingService.book(
                request.roomId,
                request.startTime,
                request.endTime,
                user
        );
    }

    // CANCEL BOOKING
    @PostMapping("/{id}/cancel")
    public Map<String, String> cancelBooking(@PathVariable Long id) {
        bookingService.cancel(id);
        return Map.of("status", "CANCELLED");
    }
}
