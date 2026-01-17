
package com.meetingspace.service;

import com.meetingspace.dto.RoomRequest;
import com.meetingspace.entity.Amenity;
import com.meetingspace.entity.Room;
import com.meetingspace.repository.AmenityRepository;
import com.meetingspace.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Test
    void create_withAmenities_success() {
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        AmenityRepository amenityRepository = Mockito.mock(AmenityRepository.class);

        Amenity a1 = new Amenity();
        Amenity a2 = new Amenity();

        when(amenityRepository.findById(1L)).thenReturn(Optional.of(a1));
        when(amenityRepository.findById(2L)).thenReturn(Optional.of(a2));

        // Return the same Room passed to save()
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomService service = new RoomService(roomRepository, amenityRepository);

        RoomRequest req = new RoomRequest();
        req.setName("Collab Area");
        req.setCapacity(12);
        req.setFloor(3);
        req.setAmenityIds(java.util.List.of(1L, 2L));

        Room r = service.create(req);

        assertEquals("Collab Area", r.getName());
        assertEquals(12, r.getCapacity());
        // NOTE: Room.java currently has no getStatus() or getFloor() -> do not assert them here

        verify(roomRepository).save(any(Room.class));
        // Optionally verify amenity lookups occurred
        verify(amenityRepository).findById(1L);
        verify(amenityRepository).findById(2L);
    }

    @Test
    void create_amenityNotFound_throws() {
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        AmenityRepository amenityRepository = Mockito.mock(AmenityRepository.class);

        when(amenityRepository.findById(1L)).thenReturn(Optional.empty());

        RoomService service = new RoomService(roomRepository, amenityRepository);

        RoomRequest req = new RoomRequest();
        req.setName("A");
        req.setCapacity(5);
        req.setFloor(1);
        req.setAmenityIds(java.util.List.of(1L));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.create(req));
        assertTrue(ex.getMessage().contains("Amenity not found"));

        // Ensure we did not persist when amenity lookup fails
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void getAll_delegatesToRepository() {
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        AmenityRepository amenityRepository = Mockito.mock(AmenityRepository.class);

        RoomService service = new RoomService(roomRepository, amenityRepository);

        service.getAll();

        verify(roomRepository).findAll();
        verifyNoInteractions(amenityRepository);
    }
}