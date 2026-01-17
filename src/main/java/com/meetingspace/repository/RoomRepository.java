package com.meetingspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meetingspace.entity.Room;

public interface RoomRepository extends JpaRepository<Room,Long> {}
