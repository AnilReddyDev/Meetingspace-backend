package com.meetingspace.controller;

import com.meetingspace.dto.RoomRequest;
import com.meetingspace.entity.Room;
import com.meetingspace.service.RoomService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminRoomController {


    private final RoomService roomService;


    public AdminRoomController(RoomService roomService) {
        super();
        this.roomService = roomService;
    }

    // CREATE ROOM
    @PostMapping
    public Room createRoom(@RequestBody RoomRequest request) {
        return roomService.create(request);
    }

    // LIST ROOMS (ADMIN VIEW)
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAll();
    }
}
