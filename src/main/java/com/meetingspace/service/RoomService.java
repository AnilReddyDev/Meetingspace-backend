package com.meetingspace.service;

import com.meetingspace.dto.RoomRequest;
import com.meetingspace.entity.*;
import com.meetingspace.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomService {


    private final RoomRepository roomRepository;

    private final AmenityRepository amenityRepository;


    public RoomService(RoomRepository roomRepository, AmenityRepository amenityRepository) {
        super();
        this.roomRepository = roomRepository;
        this.amenityRepository = amenityRepository;
    }

    // CREATE ROOM
    public Room create(RoomRequest request) {

        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setFloor(request.getFloor());
        room.setStatus("ACTIVE");

        Set<Amenity> amenities = new HashSet<>();
        if (request.getAmenityIds() != null) {
            for (Long id : request.getAmenityIds()) {
                Amenity amenity = amenityRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Amenity not found"));
                amenities.add(amenity);
            }
        }

        room.setAmenities(amenities);
        return roomRepository.save(room);
    }

    // GET ALL ROOMS
    public List<Room> getAll() {
        return roomRepository.findAll();
    }
}
