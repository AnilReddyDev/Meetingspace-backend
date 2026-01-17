
package com.meetingspace.dto;

import java.util.List;

public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private boolean active;
    private boolean verified;
    private List<String> roles; // e.g., ["ADMIN", "USER"]

    public UserProfileDto(Long id, String username, String email, boolean active, boolean verified, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.active = active;
        this.verified = verified;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public boolean isVerified() { return verified; }
    public List<String> getRoles() { return roles; }
}
