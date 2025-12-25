package com.meetingspace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String tokenHash;
    private LocalDateTime expiresAt;

    public User getUser(){ return user; }
    public String getTokenHash(){ return tokenHash; }
    public LocalDateTime getExpiresAt(){ return expiresAt; }

    public void setUser(User u){this.user=u;}
    public void setTokenHash(String t){this.tokenHash=t;}
    public void setExpiresAt(LocalDateTime e){this.expiresAt=e;}
}
