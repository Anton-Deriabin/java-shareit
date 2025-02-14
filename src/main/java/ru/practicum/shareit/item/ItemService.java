package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> findAllFromUser(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemCreateDto item, Long userId);

    ItemDto update(ItemUpdateDto newItem, Long userId, Long id);
}