package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingRequestDto bookingRequestDto, String bookerId);

    BookingDto approveBooking(String bookingId, Boolean approved, String ownerId);

    BookingDto findBooking(String bookingId, String bookerOrOwnerId);

    List<BookingDto> findBookerBookings(BookingState state, String userId);

    List<BookingDto> findOwnerBookings(BookingState state, String userId);
}
