package com.meetingspace.service;

import com.meetingspace.dto.AvailabilityResponse;
import com.meetingspace.entity.Booking;
import com.meetingspace.repository.BookingRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;

    public AvailabilityService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public AvailabilityResponse getAvailability(Long roomId, LocalDate date) {

        LocalDateTime dayStart = date.atTime(9, 0);
        LocalDateTime dayEnd = date.atTime(18, 0);

        List<Booking> bookings =
                bookingRepository.findBookingsForDate(roomId, date);

        List<AvailabilityResponse.Slot> slots = new ArrayList<>();
        LocalDateTime current = dayStart;

        for (Booking booking : bookings) {

            if (current.isBefore(booking.getStartTime())) {
                slots.add(createSlot(
                        current,
                        booking.getStartTime(),
                        "AVAILABLE"
                ));
            }

            slots.add(createSlot(
                    booking.getStartTime(),
                    booking.getEndTime(),
                    "BOOKED"
            ));

            current = booking.getEndTime();
        }

        if (current.isBefore(dayEnd)) {
            slots.add(createSlot(
                    current,
                    dayEnd,
                    "AVAILABLE"
            ));
        }

        AvailabilityResponse response = new AvailabilityResponse();
        response.setAvailability(slots);
        return response;
    }

    private AvailabilityResponse.Slot createSlot(
            LocalDateTime start,
            LocalDateTime end,
            String status
    ) {
        AvailabilityResponse.Slot slot = new AvailabilityResponse.Slot();
        slot.setStart(start.toString());
        slot.setEnd(end.toString());
        slot.setStatus(status);
        return slot;
    }
}
