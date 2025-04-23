package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.CheckItemService;
import ru.practicum.shareit.utils.CheckUserService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CheckUserService checkUserService;
    private final CheckItemService checkItemService;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, String bookerId) {
        User booker = checkUserService.checkUser(bookerId);
        Item item = checkItemService.checkItem(bookingRequestDto.getItemId());

        if (item.getOwnerId().equals(bookerId)) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.mapToBookingFromRequestDto(bookingRequestDto, booker, item);
        booking.setStatus(Status.WAITING);

        booking = bookingRepository.save(booking);

        return enrichWithDetails(booking);
    }

    @Transactional
    public BookingDto approveBooking(String bookingId, Boolean approved, String ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);

        return enrichWithDetails(booking);
    }

    public BookingDto findBooking(String bookingId, String requesterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!booking.getBookerId().equals(requesterId) && !item.getOwnerId().equals(requesterId)) {
            throw new ValidationException("Нет доступа к просмотру бронирования");
        }

        return enrichWithDetails(booking);
    }

    public List<BookingDto> findBookerBookings(BookingState state, String bookerId) {
        checkState(state);
        checkUserService.checkUser(bookerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(bookerId);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
        };

        return bookings.stream()
                .map(this::enrichWithDetails)
                .collect(Collectors.toList());
    }

    public List<BookingDto> findOwnerBookings(BookingState state, String ownerId) {
        checkState(state);
        checkUserService.checkUser(ownerId);

        List<Item> ownerItems = itemRepository.findByOwnerId(ownerId);
        List<String> itemIds = ownerItems.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemIdInOrderByStartDesc(itemIds);
            case CURRENT -> bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, now, now);
            case PAST -> bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now);
            case FUTURE -> bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
            case WAITING -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING);
            case REJECTED -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED);
        };

        return bookings.stream()
                .map(this::enrichWithDetails)
                .collect(Collectors.toList());
    }

    private BookingDto enrichWithDetails(Booking booking) {
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        dto.setBooker(new ru.practicum.shareit.user.dto.UserDto(
                booker.getId(), booker.getName(), booker.getEmail()));

        dto.setItem(new ru.practicum.shareit.item.dto.ItemDto(
                item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwnerId()));

        return dto;
    }

    private void checkState(BookingState state) {
        if (state == null || !EnumSet.allOf(BookingState.class).contains(state)) {
            throw new ValidationException("Неверный статус бронирования: " + state);
        }
    }
}

