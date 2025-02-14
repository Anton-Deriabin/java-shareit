package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsCommentsDto> findAllFromUser(Long userId);

    ItemWithCommentsDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemCreateDto item, Long userId);

    ItemDto update(ItemUpdateDto newItem, Long userId, Long id);

    CommentDto createComment(CommentCreateDto comment, Long userId, Long itemId);
}