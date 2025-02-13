package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllFromUser(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemCreateDto item, Long userId);

    ItemDto update(ItemUpdateDto newItem, Long userId, Long id);
}