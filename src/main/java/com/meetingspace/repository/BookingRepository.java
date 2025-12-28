package com.meetingspace.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.meetingspace.entity.Booking;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔹 Check overlapping bookings (PostgreSQL range operator)
    @Query(value = """
        SELECT 1 FROM bookings
        WHERE room_id = :roomId
          AND tstzrange(start_time, end_time)
          && tstzrange(:start, :end)
    """, nativeQuery = true)
    Optional<Integer> conflict(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 🔹 Count bookings for dashboard (FIXED)
    @Query(value = """
        SELECT COUNT(*)
        FROM bookings
        WHERE room_id = :roomId
          AND start_time >= :start
          AND end_time <= :end
    """, nativeQuery = true)
    long countBookingsForRoomToday(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
