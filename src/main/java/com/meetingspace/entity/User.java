package com.meetingspace.entity;

import com.meetingspace.validation.AllowedEmailDomain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @AllowedEmailDomain("hcltech.com")
    private String email;


    private String passwordHash;
    private boolean isActive = true;
    private boolean isVerified = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isVerified() { return isVerified; }
    public boolean isActive() { return isActive; }
    public Set<Role> getRoles() { return roles; }

    public void setUsername(String u){this.username=u;}
    public void setEmail(String e){this.email=e;}
    public void setPasswordHash(String p){this.passwordHash=p;}
    public void setVerified(boolean v){this.isVerified=v;}
	public String getUsername() {
		return username;
	}
	
	
}
