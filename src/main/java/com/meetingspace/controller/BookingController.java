
package com.meetingspace.controller;

import com.meetingspace.dto.BookingRequest;
import com.meetingspace.dto.BookingResponseDto;
import com.meetingspace.entity.Booking;
import com.meetingspace.entity.User;
import com.meetingspace.repository.UserRepository;
import com.meetingspace.service.BookingService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
    

    // USER BOOKINGS → "Your Schedule"
       @GetMapping("/bookings")
       public List<BookingResponseDto> getUserBookings(Authentication authentication) {
           String email = authentication.getName();
           return bookingService.getUserBookings(email);
       }



    // CREATE BOOKING
    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return bookingService.book(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime(),
                user
        );
    }

    // CANCEL BOOKING BY ID (optional)
    @PostMapping("/{id}/cancel")
    public Map<String, String> cancelBooking(@PathVariable Long id) {
        bookingService.cancel(id);
        return Map.of("status", "CANCELLED");
    }

    // CANCEL BOOKING BY SLOT (optional)
    @PostMapping("/cancel")
    public Map<String, String> cancelBySlot(@RequestBody BookingRequest request, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        bookingService.cancelBySlot(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime(),
                user
        );
        return Map.of("status", "CANCELLED");
    }
}
