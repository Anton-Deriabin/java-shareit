package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsCommentsDto> findAllFromUser(String userId);

    ItemWithCommentsDto findById(String id, String userId);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemCreateDto item, String userId);

    ItemDto update(ItemUpdateDto newItem, String userId, String id);

    CommentDto createComment(CommentCreateDto comment, String userId, String itemId);
}