package com.meetingspace.repository;

import org.springframework.data.jpa.repository.*;
import com.meetingspace.entity.Booking;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query(value="""
      SELECT 1 FROM bookings
      WHERE room_id = :roomId
      AND tstzrange(start_time, end_time)
      && tstzrange(:start, :end)
    """, nativeQuery = true)
    Optional<Integer> conflict(Long roomId,
                               LocalDateTime start, LocalDateTime end);
    long countBookingsForRoomToday(
            Long roomId,
            LocalDateTime start,
            LocalDateTime end
    );

//    long countBookingsForRoomToday(Long id, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
