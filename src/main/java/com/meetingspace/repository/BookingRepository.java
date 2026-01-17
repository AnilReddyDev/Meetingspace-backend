package com.meetingspace.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.meetingspace.entity.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status = 'CONFIRMED'
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    Optional<Booking> conflict(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // For availability (used later)
    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status = 'CONFIRMED'
          AND DATE(b.startTime) = :date
        ORDER BY b.startTime
    """)
    List<Booking> findBookingsForDate(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date
    );


    @Query("""
    SELECT b FROM Booking b
    WHERE b.room.id = :roomId
      AND b.startTime = :startTime
      AND b.endTime = :endTime
      AND b.status = 'CONFIRMED'
      AND b.user.email = :email
""")
    Optional<Booking> findForCancellationByUser(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("email") String email
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


    @Query("""
    SELECT COUNT(b) FROM Booking b
    WHERE b.room.id = :roomId
      AND b.status = 'CONFIRMED'
      AND b.startTime >= :start
      AND b.startTime < :end
""")
    long countConfirmedBookings(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT COUNT(b) FROM Booking b
    WHERE b.room.id = :roomId
      AND b.status = 'CANCELLED'
      AND b.startTime >= :start
      AND b.startTime < :end
""")
    long countCancelledBookings(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    List<Booking> findByUserEmailOrderByStartTimeAsc(String email);
    
    

@Query("""
      SELECT COUNT(b) FROM Booking b
      WHERE b.room.id = :roomId
        AND b.status = :status
        AND b.startTime < :endOfDay
        AND b.endTime > :startOfDay
      """)
    int countByRoomIdAndStatusAndOverlap(Long roomId, String status,
                                         LocalDateTime startOfDay,
                                         LocalDateTime endOfDay);


}
