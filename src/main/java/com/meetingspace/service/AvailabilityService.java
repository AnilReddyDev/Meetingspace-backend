package com.meetingspace.service;

import com.meetingspace.dto.AvailabilityResponse;
import com.meetingspace.entity.Booking;
import com.meetingspace.repository.BookingRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    private BookingRepository bookingRepository;

    // ================= ROOM AVAILABILITY =================
    public AvailabilityResponse get(Long roomId) {

        AvailabilityResponse response = new AvailabilityResponse();
        List<AvailabilityResponse.Slot> slots = new ArrayList<>();

        // Example static timeline (can be enhanced)
        AvailabilityResponse.Slot booked = new AvailabilityResponse.Slot();
        booked.start = "10:00";
        booked.end = "11:30";
        booked.status = "BOOKED";

        AvailabilityResponse.Slot available = new AvailabilityResponse.Slot();
        available.start = "11:30";
        available.end = "18:00";
        available.status = "AVAILABLE";

        slots.add(booked);
        slots.add(available);

        response.availability = slots;
        return response;
    }
}
