package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDto> findAllFromUser(Long userId) {
        return itemRepository.findAllFromUser()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", id)));
    }

    public List<ItemDto> findByText(String text) {
        return itemRepository.findByText(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto create(Item item) {
        return ItemMapper.mapToItemDto(itemRepository.create(item));
    }

    public ItemDto update(Item newItem) {
        return ItemMapper.mapToItemDto(itemRepository.update(newItem));
    }
}
