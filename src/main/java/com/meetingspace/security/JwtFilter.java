package com.meetingspace.security;

import com.meetingspace.repository.UserRepository;
import com.meetingspace.entity.User;

import org.springframework.stereotype.Component;
import org.springframework.security.core.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;


@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;



    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        super();
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {

                String email = jwtUtil.extractEmail(token);

                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {

                    var authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                            .toList();

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
