
package com.meetingspace.service;

import com.meetingspace.entity.Booking;
import com.meetingspace.entity.Room;
import com.meetingspace.entity.User;
import com.meetingspace.repository.BookingRepository;
import com.meetingspace.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Test
    void book_success() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        when(bookingRepository.conflict(anyLong(), any(), any())).thenReturn(Optional.empty());

        Room room = new Room();
        when(roomRepository.findById(7L)).thenReturn(Optional.of(room));

        BookingService service = new BookingService(bookingRepository, roomRepository);
        User user = new User(); user.setEmail("alice@hcltech.com");

        LocalDateTime start = LocalDateTime.of(2025,1,1,10,0);
        LocalDateTime end   = LocalDateTime.of(2025,1,1,11,0);

        Booking saved = new Booking();
        saved.setStatus("CONFIRMED");
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);

        Booking b = service.book(7L, start, end, user);
        assertEquals("CONFIRMED", b.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void book_failsOnInvalidTimes() {
        BookingService service = new BookingService(Mockito.mock(BookingRepository.class), Mockito.mock(RoomRepository.class));
        LocalDateTime t = LocalDateTime.now();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            service.book(7L, t, t, new User())
        );
        assertTrue(ex.getMessage().contains("Start time must be before end time"));
    }

 

    @Test
    void cancel_success() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
        BookingService service = new BookingService(bookingRepository, roomRepository);

        Booking b = new Booking();
        b.setStatus("CONFIRMED");
        when(bookingRepository.findById(99L)).thenReturn(Optional.of(b));
        service.cancel(99L);
        assertEquals("CANCELLED", b.getStatus());
        verify(bookingRepository).save(b);
    }

    @Test
    void cancel_alreadyCancelled_throws() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        BookingService service = new BookingService(bookingRepository, Mockito.mock(RoomRepository.class));
        Booking b = new Booking(); b.setStatus("CANCELLED");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(b));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cancel(1L));
        assertTrue(ex.getMessage().contains("already cancelled"));
    }

    @Test
    void cancelBySlot_success() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);
        BookingService service = new BookingService(repo, Mockito.mock(RoomRepository.class));
        Booking b = new Booking(); b.setStatus("CONFIRMED");
        when(repo.findForCancellationByUser(eq(7L), any(), any(), eq("alice@hcltech.com")))
            .thenReturn(Optional.of(b));
        User user = new User(); user.setEmail("alice@hcltech.com");
        service.cancelBySlot(7L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), user);
        assertEquals("CANCELLED", b.getStatus());
        verify(repo).save(b);
    }

    @Test
    void cancelBySlot_noActiveBooking_throws() {
        BookingRepository repo = Mockito.mock(BookingRepository.class);
        BookingService service = new BookingService(repo, Mockito.mock(RoomRepository.class));
        when(repo.findForCancellationByUser(anyLong(), any(), any(), anyString()))
            .thenReturn(Optional.empty());
        User user = new User(); user.setEmail("alice@hcltech.com");
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            service.cancelBySlot(7L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), user)
        );
        assertTrue(ex.getMessage().contains("No active booking"));
    }
}
