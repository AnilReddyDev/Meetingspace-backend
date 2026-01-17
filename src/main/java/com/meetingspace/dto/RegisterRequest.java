package com.meetingspace.dto;

import com.meetingspace.validation.AllowedEmailDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    private String username;
    @NotBlank
    @Email
    @AllowedEmailDomain("hcltech.com")
    private String email;
    private String password;

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
