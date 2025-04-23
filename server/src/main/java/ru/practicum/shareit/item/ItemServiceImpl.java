package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.CheckItemService;
import ru.practicum.shareit.utils.CheckUserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CheckUserService checkUserService;
    private final CheckItemService checkItemService;
    private final ItemRequestRepository itemRequestRepository;

    public List<ItemWithBookingsCommentsDto> findAllFromUser(String userId) {
        checkUserService.checkUser(userId);

        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<String> itemIds = userItems.stream().map(Item::getId).toList();

        List<Booking> bookings = bookingRepository.findByItemIdInOrderByStartDesc(itemIds);

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        Map<String, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItemId));
        Map<String, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItemId));

        List<String> authorIds = comments.stream()
                .map(Comment::getAuthorId).distinct().toList();
        Map<String, User> authors = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return userItems.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), List.of());
                    List<User> commentAuthors = itemComments.stream()
                            .map(c -> authors.get(c.getAuthorId()))
                            .filter(Objects::nonNull)
                            .toList();
                    return ItemMapper.mapToItemWithBookingsCommentsDto(item, itemBookings, itemComments, commentAuthors);
                })
                .toList();
    }


    public ItemWithCommentsDto findById(String id, String userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.error("Вещь с id = {} не найдена", id);
            return new NotFoundException(String.format("Вещь с id=%s не найдена", id));
        });

        List<Comment> comments = commentRepository.findByItemId(id); // ← заменили
        List<String> authorIds = comments.stream().map(Comment::getAuthorId).distinct().toList();
        Map<String, User> authors = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        ItemWithCommentsDto dto = ItemMapper.mapToItemWithCommentsDto(
                item,
                comments,
                comments.stream()
                        .map(c -> authors.get(c.getAuthorId()))
                        .filter(Objects::nonNull)
                        .toList()
        );

        if (item.getOwnerId().equals(userId)) {
            List<String> itemIds = itemRepository.findByOwnerId(userId).stream().map(Item::getId).toList();
            List<Booking> bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.APPROVED);

            dto.setLastBooking(bookings.stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .findFirst().map(BookingMapper::mapToBookingDto).orElse(null));

            dto.setNextBooking(bookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().map(BookingMapper::mapToBookingDto).orElse(null));
        }

        return logAndReturn(dto, d -> log.info("Вещь с id = {} с комментариями найдена", d.getId()));
    }

    public List<ItemDto> findByText(String text) {
        if (text == null || text.isBlank()) return List.of();

        List<ItemDto> result = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .toList();

        log.info("Найдено {} вещей по тексту '{}'", result.size(), text);
        return result;
    }

    @Transactional
    public ItemDto create(ItemCreateDto itemDto, String userId) {
        User owner = checkUserService.checkUser(userId);
        ItemRequest request = itemDto.getRequestId() != null ?
                itemRequestRepository.findById(itemDto.getRequestId()).orElse(null) : null;

        Item item = ItemMapper.mapToItemFromCreateDto(itemDto, owner, request);
        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(item)),
                i -> log.info("Создана вещь с id = {}", i.getId())
        );
    }

    @Transactional
    public ItemDto update(ItemUpdateDto itemDto, String userId, String id) {
        User owner = checkUserService.checkUser(userId);
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", id);
            return new NotFoundException(String.format("Вещь с id=%s не найдена", id));
        });

        if (!item.getOwnerId().equals(owner.getId())) {
            throw new ValidationException("Изменять может только владелец");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(item)),
                i -> log.info("Обновлена вещь с id = {}", i.getId())
        );
    }

    @Transactional
    public CommentDto createComment(CommentCreateDto comment, String userId, String itemId) {
        User commentator = checkUserService.checkUser(userId);
        Item item = checkItemService.checkItem(itemId);
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findByBookerId(userId).stream()
                .filter(b -> b.getItemId().equals(itemId))
                .filter(b -> b.getStatus().equals(Status.APPROVED) && b.getEnd().isBefore(now))
                .findAny()
                .orElseThrow(() -> new ValidationException(
                        String.format("Пользователь с id = %s не арендовал вещь %s", userId, itemId)));

        return logAndReturn(
                CommentMapper.mapToCommentDto(
                        commentRepository.save(
                                CommentMapper.mapToCommentFromCreate(comment, commentator, item)
                        ),
                        commentator.getName()
                ),
                c -> log.info("Комментарий с id = {} добавлен к вещи {}", c.getId(), itemId)
        );
    }
}

