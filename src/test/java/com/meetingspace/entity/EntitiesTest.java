
package com.meetingspace.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntitiesTest {

    @Test
    void user_defaultsAndSetters() {
        User u = new User();
        u.setUsername("uday");
        u.setEmail("uday@hcltech.com");
        u.setPasswordHash("hashed");

        assertEquals("uday@hcltech.com", u.getEmail());

        // Assuming defaults based on your earlier test intent
        assertTrue(u.isActive());
        assertFalse(u.isVerified());

        u.setVerified(true);
        assertTrue(u.isVerified());
    }

    @Test
    void room_defaultsAndSetters() {
        Room r = new Room();
        r.setName("A");
        r.setCapacity(5);
        r.setFloor(1);
        r.setStatus("ACTIVE");

        assertEquals("A", r.getName());
        assertEquals(5, r.getCapacity());

        // NOTE:
        // Room.java currently does not define getStatus() or getFloor() getters.
        // If you add them later:
        //   assertEquals("ACTIVE", r.getStatus());
        //   assertEquals(1, r.getFloor());
    }

    @Test
    void booking_settersAndGetters() {
        Booking b = new Booking();
        b.setStatus("CONFIRMED");
        assertEquals("CONFIRMED", b.getStatus());
    }

    @Test
    void verificationToken_setters() {
        VerificationToken vt = new VerificationToken();
        User u = new User();
        vt.setUser(u);
        vt.setTokenHash("hash");
        java.time.LocalDateTime t = java.time.LocalDateTime.now();
        vt.setExpiresAt(t);

        assertEquals(u, vt.getUser());
        assertEquals("hash", vt.getTokenHash());
        assertEquals(t, vt.getExpiresAt());
    }

    @Test
    void amenity_basic() {
        Amenity a = new Amenity();
        assertNull(a.getId());
    }

    @Test
    void role_basic() {
        Role r = new Role();
        assertNull(r.getName());
    }
}
