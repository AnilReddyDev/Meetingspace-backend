
package com.meetingspace.security;

import com.meetingspace.entity.Role;
import com.meetingspace.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateAndValidateToken_extractEmailAndRoles() {
        JwtUtil jwtUtil = new JwtUtil("meetingspace-secret-key-123456789012", 1000 * 60);

        User user = new User();
        user.setEmail("uday@hcltech.com");
        Role r1 = new Role();
        Role r2 = new Role();
        user.getRoles().add(r1);
        user.getRoles().add(r2);

        String token = jwtUtil.generateToken(user);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("uday@hcltech.com", jwtUtil.extractEmail(token));
        assertNotNull(jwtUtil.extractRoles(token));
    }

    @Test
    void validateToken_expired_returnsFalse() throws InterruptedException {
        JwtUtil jwtUtil = new JwtUtil("meetingspace-secret-key-123456789012", 1);
        User user = new User(); user.setEmail("uday@hcltech.com");
        String token = jwtUtil.generateToken(user);
        Thread.sleep(5);
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_invalidSignature_returnsFalse() {
        JwtUtil utilA = new JwtUtil("meetingspace-secret-key-123456789012", 10000);
        JwtUtil utilB = new JwtUtil("another-secret-key-9876543210abcd", 10000);
        User user = new User(); user.setEmail("uday@hcltech.com");
        String token = utilA.generateToken(user);
        assertFalse(utilB.validateToken(token));
    }
}
