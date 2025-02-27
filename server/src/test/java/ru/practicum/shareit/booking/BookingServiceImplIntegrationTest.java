package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingServiceImplIntegrationTest extends BaseSpringBootTest {

    private User booker;
    private User itemOwner;
    private Item item;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    public void setUp() {
        itemOwner = new User(null, "Item Owner", "owner@example.com");
        userRepository.save(itemOwner);
        booker = new User(null, "Booker", "booker@example.com");
        userRepository.save(booker);
        item = new Item(null, "Item Name", "Item Description", true, itemOwner, null);
        itemRepository.save(item);
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setBookerId(booker.getId());
    }

    @Test
    public void testCreateBookingWhenValidRequestThenReturnBookingDto() {
        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(createdBooking.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testCreateBookingWhenItemNotAvailableThenThrowValidationException() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequestDto, booker.getId()));
    }

    @Test
    public void testFindBookingWhenBookingExistsThenReturnBookingDto() {
        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());
        BookingDto foundBooking = bookingService.findBooking(createdBooking.getId(), booker.getId());
        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    public void testFindBookingWhenBookingDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.findBooking(999L, booker.getId()));
    }

    @Test
    public void testApproveBookingWhenOwnerApprovesThenReturnUpdatedBookingDto() {
        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());
        BookingDto approvedBooking = bookingService.approveBooking(createdBooking.getId(), true, itemOwner.getId());
        assertThat(approvedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    public void testApproveBookingWhenNotOwnerThenThrowValidationException() {
        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(createdBooking.getId(), true, 999L));
    }

    @Test
    public void testFindBookerBookingsWhenBookingsExistThenReturnBookingDtos() {
        bookingService.createBooking(bookingRequestDto, booker.getId());
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.ALL, booker.getId());
        assertThat(bookings).isNotEmpty();
    }

    @Test
    public void testFindOwnerBookingsWhenBookingsExistThenReturnBookingDtos() {
        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());
        List<BookingDto> ownerBookings = bookingService.findOwnerBookings(BookingState.ALL, itemOwner.getId());
        assertThat(ownerBookings).isNotEmpty();
        assertThat(ownerBookings.getFirst().getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    public void testFindBookerBookingsWhenNoBookingsExistThenReturnEmptyList() {
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.ALL, booker.getId());
        assertThat(bookings).isEmpty();
    }

    @Test
    public void testFindOwnerBookingsWhenNoBookingsExistThenReturnEmptyList() {
        List<BookingDto> ownerBookings = bookingService.findOwnerBookings(BookingState.ALL, itemOwner.getId());
        assertThat(ownerBookings).isEmpty();
    }
}
