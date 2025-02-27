package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckItemService;
import ru.practicum.shareit.utils.CheckUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CheckUserService checkUserService;

    @Mock
    private CheckItemService checkItemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User itemOwner;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "John Doe", "john.doe@example.com");
        itemOwner = new User(2L, "Item Owner", "owner@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, itemOwner, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                item, booker, Status.APPROVED);
        bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId(), booker.getId());
    }

    @Test
    void testCreateBookingWhenValidRequestThenReturnBookingDto() {
        when(checkUserService.checkUser(booker.getId())).thenReturn(booker);
        when(checkItemService.checkItem(item.getId())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto result = bookingService.createBooking(bookingRequestDto, booker.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testCreateBookingWhenUserNotFoundThenThrowException() {
        when(checkUserService.checkUser(booker.getId())).thenThrow(new NotFoundException("User not found"));
        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testCreateBookingWhenItemNotFoundThenThrowException() {
        when(checkUserService.checkUser(booker.getId())).thenReturn(booker);
        when(checkItemService.checkItem(item.getId())).thenThrow(new NotFoundException("Item not found"));
        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item not found");
    }

    @Test
    void testApproveBookingWhenOwnerApprovesThenReturnUpdatedBookingDto() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking); // Настраиваем возврат сохраненного объекта
        BookingDto result = bookingService.approveBooking(booking.getId(), true, item.getOwner().getId());
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    void testApproveBookingWhenNotOwnerThenThrowException() {
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> bookingService.approveBooking(booking.getId(), true, 999L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не является владельцем вещи");
    }

    @Test
    void testFindBookingWhenBookingExistsThenReturnBookingDto() {
        when(checkUserService.checkUser(booker.getId())).thenReturn(booker);
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.findBooking(booking.getId(), booker.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindBookingWhenBookingDoesNotExistThenThrowException() {
        when(checkUserService.checkUser(booker.getId())).thenReturn(booker);
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.findBooking(booking.getId(), booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование с id=");
    }

    @Test
    void testFindBookingWhenUserIsNotBookerOrOwnerThenThrowException() {
        User anotherUser = new User(2L, "Jane Doe", "jane.doe@example.com");
        when(checkUserService.checkUser(anotherUser.getId())).thenReturn(anotherUser);
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> bookingService.findBooking(booking.getId(), anotherUser.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь не является владельцем вещи или автором бронирования");
    }

    @Test
    void testFindBookerBookingsForAllState() {
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdWithDetails(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.ALL, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
        verify(bookingRepository).findByBookerIdWithDetails(eq(booker.getId()));
    }

    @Test
    void testFindBookerBookingsForCurrentState() {
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(2L, now.minusHours(1), now.plusHours(1), item, booker, Status.APPROVED);
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.CURRENT, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testFindBookerBookingsForPastState() {
        LocalDateTime past = LocalDateTime.now().minusDays(2);
        Booking pastBooking = new Booking(3L, past.minusDays(1), past, item, booker, Status.APPROVED);
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(pastBooking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.PAST, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getEnd()).isBefore(LocalDateTime.now());
    }

    @Test
    void testFindBookerBookingsForFutureState() {
        LocalDateTime future = LocalDateTime.now().plusDays(2);
        Booking futureBooking = new Booking(4L, future, future.plusDays(1), item, booker, Status.WAITING);
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(futureBooking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.FUTURE, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStart()).isAfter(LocalDateTime.now());
    }

    @Test
    void testFindBookerBookingsForWaitingState() {
        Booking waitingBooking = new Booking(5L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.WAITING);
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.WAITING)))
                .thenReturn(List.of(waitingBooking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.WAITING, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void testFindBookerBookingsForRejectedState() {
        Booking rejectedBooking = new Booking(6L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.REJECTED);
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.REJECTED)))
                .thenReturn(List.of(rejectedBooking));
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.REJECTED, booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testFindBookerBookingsWithNoBookingsThenReturnEmptyList() {
        when(checkUserService.checkUser(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdWithDetails(anyLong())).thenReturn(Collections.emptyList());
        List<BookingDto> bookings = bookingService.findBookerBookings(BookingState.ALL, booker.getId());
        assertThat(bookings).isEmpty();
    }

    @Test
    void testFindBookerBookingsWithNullStateThenThrowException() {
        assertThatThrownBy(() -> bookingService.findBookerBookings(null, booker.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Статус бронирования не указан");
    }

    @Test
    void testFindBookerBookingsWithInvalidStateThenThrowException() {
        assertThatThrownBy(() -> bookingService.findBookerBookings(BookingState.valueOf("INVALID"), booker.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant ru.practicum.shareit.booking.BookingState.INVALID");
    }

    @Test
    void testFindOwnerBookingsForAllState() {
        when(checkUserService.checkUser(anyLong())).thenReturn(itemOwner);
        when(bookingRepository.findByItemOwnerIdWithDetails(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.findOwnerBookings(BookingState.ALL, itemOwner.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
        verify(bookingRepository).findByItemOwnerIdWithDetails(eq(itemOwner.getId()));
    }

    @Test
    void testFindOwnerBookingsForCurrentState() {
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(2L, now.minusHours(1), now.plusHours(1), item, booker, Status.APPROVED);
        when(checkUserService.checkUser(anyLong())).thenReturn(itemOwner);
        when(bookingRepository.findByItemOwner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));
        List<BookingDto> bookings = bookingService.findOwnerBookings(BookingState.CURRENT, itemOwner.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testFindOwnerBookingsWithNoBookingsThenReturnEmptyList() {
        when(checkUserService.checkUser(anyLong())).thenReturn(itemOwner);
        when(bookingRepository.findByItemOwnerIdWithDetails(anyLong())).thenReturn(Collections.emptyList());
        List<BookingDto> bookings = bookingService.findOwnerBookings(BookingState.ALL, itemOwner.getId());
        assertThat(bookings).isEmpty();
    }
}