package com.meetingspace.controller;

import com.meetingspace.service.AvailabilityService;
import com.meetingspace.dto.AvailabilityResponse;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin
public class AvailabilityController {


    private final AvailabilityService availabilityService;



    public AvailabilityController(AvailabilityService availabilityService) {
        super();
        this.availabilityService = availabilityService;
    }



    // ROOM AVAILABILITY (HOME PAGE)
    @GetMapping("/{roomId}")
    public AvailabilityResponse getAvailability(
            @PathVariable Long roomId,
            @RequestParam LocalDate date
    ) {
        return availabilityService.getAvailability(roomId, date);
    }
}
