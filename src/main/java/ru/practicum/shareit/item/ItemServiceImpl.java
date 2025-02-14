package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckItemService;
import ru.practicum.shareit.utils.CheckUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CheckUserService checkUserService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CheckItemService checkItemService;

    public List<ItemWithBookingsCommentsDto> findAllFromUser(Long userId) {
        checkUserService.checkUser(userId);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findBookingsByOwnerId(userId);
        List<Comment> comments = commentRepository.findCommentsByOwnerId(userId);
        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        List<ItemWithBookingsCommentsDto> itemDtos = userItems.stream()
                .map(item -> ItemMapper.mapToItemWithBookingsCommentsDto(
                        item,
                        bookingsByItem.getOrDefault(item.getId(), List.of()),
                        commentsByItem.getOrDefault(item.getId(), List.of())
                ))
                .toList();
        log.info("Получено {} вещей пользователя", itemDtos.size());
        return itemDtos;
    }

    public ItemWithCommentsDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id=%d не найдена", id));
                });
        List<Comment> comments = commentRepository.findCommentsByItemId(id);
        log.info("Получены комментарии для item {}: {}", id, comments);
        return logAndReturn(ItemMapper.mapToItemWithCommentsDto(item, comments),
                foundItem -> log.info("Вещь с id = {} с комментариями в количестве {} найдена",
                        foundItem.getId(), comments.size())
        );
    }

    public List<ItemDto> findByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<ItemDto> itemDtos = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .toList();
        log.info("Получено {} вещей по текстовой подстроке", itemDtos.size());
        return itemDtos;
    }

    @Transactional
    public ItemDto create(ItemCreateDto itemDto, Long userId) {
        User owner = checkUserService.checkUser(userId);
        Item item = ItemMapper.mapToItemFromCreateDto(itemDto, owner);
        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(item)),
                savedItem -> log.info("Вещь с id = {} добавлена", savedItem.getId())
        );
    }

    @Transactional
    public ItemDto update(ItemUpdateDto itemDto, Long userId, Long id) {
        User owner = checkUserService.checkUser(userId);
        ItemDto oldItem = ItemMapper.mapToItemDto(itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", id);
            return new NotFoundException(String.format("Вещь с id=%d не найдена", id));
        }));
        log.trace("Создали переменную старой вещи для обновления");
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
            log.debug("Вещи с id = {} установлено имя - {}", oldItem.getId(), oldItem.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
            log.debug("Вещи с id = {} установлено описание - {}", oldItem.getId(), oldItem.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
            log.debug("Вещи с id = {} установлена доступность - {}", oldItem.getId(), oldItem.getAvailable());
        }
        log.info("Вещь \"{}\" с id = {} - обновлена", oldItem.getName(), oldItem.getId());
        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItemFromDto(oldItem, owner))),
                savedItem -> log.info("Вещь \"{}\" с id = {} обновлена", savedItem.getName(), savedItem.getId())
                );
    }

    @Transactional
    public CommentDto createComment(CommentCreateDto comment, Long userId, Long itemId) {
        User commentator = checkUserService.checkUser(userId);
        Item item = checkItemService.checkItem(itemId);
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.findByBookerIdWithItem(userId).stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                        &&  booking.getEnd().isBefore(now))
                .findAny()
                .orElseThrow(() -> new ValidationException("Пользователь не был или не является арендатором вещи"));
        return logAndReturn(
                CommentMapper.mapToCommentDto(
                        commentRepository.save(CommentMapper.mapToCommentFromCreate(comment, commentator, item))),
                savedComment -> log.info("Комментарий с id = {} пользователя " +
                        "с id = {} вещи с id = {} добавлен", savedComment.getId(), userId, itemId)
        );
    }
}
