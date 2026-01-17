
package com.meetingspace.security;

import com.meetingspace.entity.Role;
import com.meetingspace.entity.User;
import com.meetingspace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    @Test
    void doFilter_validToken_setsAuthentication() throws Exception {
        JwtUtil jwtUtil = new JwtUtil("meetingspace-secret-key-123456789012", 60000);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        User user = new User();
        user.setEmail("admin@hcltech.com");
        Role adminRole = new Role();
        user.getRoles().add(adminRole);

        Mockito.when(userRepository.findByEmail("admin@hcltech.com")).thenReturn(Optional.of(user));

        String token = jwtUtil.generateToken(user);

        JwtFilter filter = new JwtFilter(jwtUtil, userRepository);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin@hcltech.com", SecurityContextHolder.getContext().getAuthentication().getName());
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_invalidToken_doesNotAuthenticate() throws Exception {
        JwtUtil jwtUtil = new JwtUtil("meetingspace-secret-key-123456789012", 60000);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        JwtFilter filter = new JwtFilter(jwtUtil, userRepository);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + "bad.token.value");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
