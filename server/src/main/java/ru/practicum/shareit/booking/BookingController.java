package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String bookingIdPath = "/{bookingId}";
    private final String ownerIdPath = "/owner";
    private final String userIdHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                    @RequestHeader(value = userIdHeader, required = false) Long bookerId) {
        return bookingService.createBooking(bookingRequestDto, bookerId);
    }

    @PatchMapping(bookingIdPath)
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(value = userIdHeader, required = false) Long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping(bookingIdPath)
    public BookingDto findBooking(@PathVariable Long bookingId,
                                  @RequestHeader(value = userIdHeader, required = false) Long bookerOrOwnerId) {
        return bookingService.findBooking(bookingId, bookerOrOwnerId);
    }

    @GetMapping()
    public List<BookingDto> findBookerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                            @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return bookingService.findBookerBookings(state, userId);
    }

    @GetMapping(ownerIdPath)
    public List<BookingDto> findOwnerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                             @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return bookingService.findOwnerBookings(state, userId);
    }
}

