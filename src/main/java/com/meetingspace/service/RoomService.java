package com.meetingspace.service;

import com.meetingspace.dto.RoomRequest;
import com.meetingspace.entity.*;
import com.meetingspace.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    // ================= CREATE ROOM =================
    public Room create(RoomRequest request) {

        Room room = new Room();
        room.setName(request.name);
        room.setCapacity(request.capacity);
        room.setFloor(request.floor);
        room.setStatus("ACTIVE");

        Set<Amenity> amenities = new HashSet<>();
        if (request.amenityIds != null) {
            for (Long id : request.amenityIds) {
                Amenity amenity = amenityRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Amenity not found"));
                amenities.add(amenity);
            }
        }

        room.setAmenities(amenities);
        return roomRepository.save(room);
    }

    // ================= GET ALL ROOMS =================
    public List<Room> getAll() {
        return roomRepository.findAll();
    }
}
