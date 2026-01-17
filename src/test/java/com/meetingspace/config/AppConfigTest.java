
package com.meetingspace.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void passwordEncoder_encodesAndMatches() {
        AppConfig config = new AppConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "Strong@123!";
        String enc = encoder.encode(raw);
        assertNotEquals(raw, enc);
        assertTrue(encoder.matches(raw, enc));
    }
}
