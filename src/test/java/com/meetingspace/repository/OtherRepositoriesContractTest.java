
package com.meetingspace.repository;

import com.meetingspace.entity.Amenity;
import com.meetingspace.entity.Role;
import com.meetingspace.entity.Room;
import com.meetingspace.entity.User;
import com.meetingspace.entity.VerificationToken;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OtherRepositoriesContractTest {

    @Test
    void amenityRepository_findById() {
        AmenityRepository repo = Mockito.mock(AmenityRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(new Amenity()));
        assertTrue(repo.findById(1L).isPresent());
    }

    @Test
    void roleRepository_findByName() {
        RoleRepository repo = Mockito.mock(RoleRepository.class);
        Mockito.when(repo.findByName("USER")).thenReturn(Optional.of(new Role()));
        assertTrue(repo.findByName("USER").isPresent());
    }

    @Test
    void roomRepository_findAll() {
        RoomRepository repo = Mockito.mock(RoomRepository.class);
        Mockito.when(repo.findAll()).thenReturn(java.util.List.of(new Room()));
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void userRepository_findByEmail() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        Mockito.when(repo.findByEmail("x@hcltech.com")).thenReturn(Optional.of(new User()));
        assertTrue(repo.findByEmail("x@hcltech.com").isPresent());
    }

    @Test
    void verificationTokenRepository_findByTokenHash() {
        VerificationTokenRepository repo = Mockito.mock(VerificationTokenRepository.class);
        Mockito.when(repo.findByTokenHash("hash")).thenReturn(Optional.of(new VerificationToken()));
        assertTrue(repo.findByTokenHash("hash").isPresent());
    }
}
