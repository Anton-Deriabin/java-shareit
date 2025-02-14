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

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

import ru.practicum.shareit.utils.CheckUserService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CheckUserService checkUserService;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        User booker = checkUserService.checkUser(bookerId);
        Item item = checkItem(bookingRequestDto.getItemId());
        return logAndReturn(
                BookingMapper.mapToBookingDto(bookingRepository
                        .save(BookingMapper.mapToBookingFromRequestDto(bookingRequestDto, booker, item))),
                savedBooking -> log.info("Запрос бронирования с id = {} добавлен", savedBooking.getId())
        );
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = checkBooking(bookingId);
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        return logAndReturn(
                BookingMapper.mapToBookingDto(bookingRepository
                        .save(bookingRepository.save(booking))), savedBooking ->
                        log.info("Статус {} установлен бронированию с id = {}, владельцем вещи с id = {}",
                                savedBooking.getStatus(), savedBooking.getId(),
                                ownerId)
        );
    }

    public BookingDto findBooking(Long bookingId, Long bookerOrOwnerId) {
        User bookerOrOwner = checkUserService.checkUser(bookerOrOwnerId);
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (!booking.getItem().getOwner().equals(bookerOrOwner) && !booking.getBooker().equals(bookerOrOwner)) {
            throw new ValidationException("Пользователь не является владельцем вещи или автором бронирования");
        }
        return logAndReturn(
                BookingMapper.mapToBookingDto(booking),
                foundBooking -> log.info("Бронирование с id = {} получено пользователем с id = {}",
                        foundBooking.getId(), bookerOrOwner.getId())
        );
    }

    public List<BookingDto> findBookerBookings(BookingState state, Long bookerId) {
        checkState(state);
        checkUserService.checkUser(bookerId);
        List<Booking> bookings;
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByBookerIdWithDetails(bookerId);
        } else {
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (state) {
                case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now,
                        now);
                case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                default -> throw new ValidationException("Некорректное состояние бронирования: " + state);
            };
        }
        log.info("Получено {} бронирований ({}) для пользователя с id = {}", bookings.size(), state, bookerId);
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    public List<BookingDto> findOwnerBookings(BookingState state, Long ownerId) {
        checkState(state);
        checkUserService.checkUser(ownerId);
        List<Booking> bookings;
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByItemOwnerIdWithDetails(ownerId);
        } else {
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (state) {
                case CURRENT -> bookingRepository.findByItemOwner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                        now, now);
                case PAST -> bookingRepository.findByItemOwner_IdAndEndBeforeOrderByStartDesc(ownerId, now);
                case FUTURE -> bookingRepository.findByItemOwner_IdAndStartAfterOrderByStartDesc(ownerId, now);
                case WAITING -> bookingRepository.findByItemOwner_IdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                case REJECTED -> bookingRepository.findByItemOwner_IdAndStatusOrderByStartDesc(ownerId,
                        Status.REJECTED);
                default -> throw new ValidationException("Некорректное состояние бронирования: " + state);
            };
        }
        log.info("Получено {} бронирований ({}) для владельца с id = {}", bookings.size(), state, ownerId);
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    private Item checkItem(Long itemId) {
        if (itemId == null) {
            log.error("Id вещи не указан");
            throw new ValidationException("Id вещи должен быть указан");
        }
        Item item = itemRepository.findByIdWithOwner(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        if (!item.getAvailable()) {
            log.error("Вещь не доступна для бронирования");
            throw new ValidationException("Вещь должна быть доступна для бронирования");
        }
        return item;
    }

    private Booking checkBooking(Long bookingId) {
        if (bookingId == null) {
            log.error("Id бронирования не указан");
            throw new ValidationException("Id бронирования должен быть указан");
        }
        return bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));
    }

    private void checkState(BookingState state) {
        if (state == null) {
            log.error("Статус бронирования не указан");
            throw new ValidationException("Статус бронирования должен быть указан");
        }
        if (!EnumSet.allOf(BookingState.class).contains(state)) {
            log.error("Некорректный статус бронирования: {}. Допустимые значения: {}",
                    state, EnumSet.allOf(BookingState.class));
            throw new ValidationException("Передан некорректный статус бронирования: " + state);
        }
    }
}
