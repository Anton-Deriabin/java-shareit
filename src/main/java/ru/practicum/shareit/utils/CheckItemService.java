package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckItemService {
    private final ItemRepository itemRepository;

    public Item checkItem(Long itemId) {
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
}
