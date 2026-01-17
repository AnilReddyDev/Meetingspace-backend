
package com.meetingspace.repository;

import com.meetingspace.entity.Booking;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingRepositoryContractTest {

    @Test
    void conflict_returnsOptional() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);

        // Use fixed times to avoid stubbing mismatch with multiple LocalDateTime.now() calls
        LocalDateTime start = LocalDateTime.parse("2025-01-01T10:00:00");
        LocalDateTime end   = start.plusHours(1);

        Mockito.when(repo.conflict(7L, start, end))
               .thenReturn(Optional.of(new Booking()));

        assertTrue(repo.conflict(7L, start, end).isPresent(),
                "Expected conflict() to return a non-empty Optional");
    }

    @Test
    void findBookingsForDate_returnsList() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);

        LocalDate date = LocalDate.of(2025, 1, 1);

        Mockito.when(repo.findBookingsForDate(7L, date))
               .thenReturn(java.util.List.of(new Booking()));

        assertEquals(1, repo.findBookingsForDate(7L, date).size(),
                "Expected one booking returned for the given date");
    }

    @Test
    void findForCancellationByUser_returnsEmptyWhenNoMatch() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);

        LocalDateTime start = LocalDateTime.parse("2025-01-01T10:00:00");
        LocalDateTime end   = start.plusHours(1);
        String email = "x@hcltech.com";

        Mockito.when(repo.findForCancellationByUser(7L, start, end, email))
               .thenReturn(Optional.empty());

        assertTrue(repo.findForCancellationByUser(7L, start, end, email).isEmpty(),
                "Expected empty Optional when no matching booking exists");
    }
}
