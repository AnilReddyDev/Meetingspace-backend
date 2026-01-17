package com.meetingspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meetingspace.entity.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity,Long> {}
