package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId);

    BookingDto findBooking(Long bookingId, Long bookerOrOwnerId);

    List<BookingDto> findBookerBookings(BookingState state, Long userId);

    List<BookingDto> findOwnerBookings(BookingState state, Long userId);
}
