package com.meetingspace.controller;

import com.meetingspace.service.AvailabilityService;
import com.meetingspace.dto.AvailabilityResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/rooms")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    // ROOM AVAILABILITY (HOME PAGE)
    @GetMapping("/{roomId}/availability")
    public AvailabilityResponse getAvailability(@PathVariable Long roomId) {
        return availabilityService.get(roomId);
    }
}
