package com.meetingspace.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meetingspace.dto.UserProfileDto;
import com.meetingspace.entity.Room;
import com.meetingspace.entity.User;
import com.meetingspace.repository.UserRepository;
import com.meetingspace.service.RoomService;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
public class UserController {
	
	private final RoomService roomService;
	private final UserRepository userRepository;

    public UserController(RoomService roomService,UserRepository userRepository) {
        super();
        this.roomService = roomService;
        this.userRepository=userRepository;
    }
	// LIST OF ROOMS 
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAll();
    }
    
    // Get current user profile (based on JWT principal email)
    @GetMapping("/me")
    public UserProfileDto me(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map roles -> List<String>
        List<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getName()) // assumes Role has getName()
                .toList();

        return new UserProfileDto(
                user.getId(),
                user.getUsername(),   // you have username
                user.getEmail(),
                user.isActive(),
                user.isVerified(),
                roleNames
        );
    }
   

    
	

}








