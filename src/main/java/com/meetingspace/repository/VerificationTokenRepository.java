package com.meetingspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meetingspace.entity.VerificationToken;
import java.util.Optional;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken,Long> {

    Optional<VerificationToken> findByTokenHash(String tokenHash);
}
