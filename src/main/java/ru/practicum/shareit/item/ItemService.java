package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllFromUser(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(Item item, Long userId);

    ItemDto update(Item newItem, Long userId, Long id);
}

