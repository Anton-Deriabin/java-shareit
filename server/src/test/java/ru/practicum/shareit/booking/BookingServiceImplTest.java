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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "John Doe", "john.doe@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, booker, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker,
                Status.APPROVED);
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
}
