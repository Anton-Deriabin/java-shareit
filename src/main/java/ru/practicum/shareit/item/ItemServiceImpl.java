package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> findAllFromUser(Long userId) {
        checkOwner(userId);
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
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByText(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto create(Item item, Long userId) {
        checkOwner(userId);
        //item.setOwner(userId);
        checkName(item);
        checkDescription(item);
        checkAvailable(item);
        return ItemMapper.mapToItemDto(itemRepository.create(item));
    }

    public ItemDto update(Item newItem, Long userId, Long id) {
        checkOwner(userId);
        //newItem.setOwner(userId);
        newItem.setId(id);
        checkItemExists(newItem.getId());
        return ItemMapper.mapToItemDto(itemRepository.update(newItem));
    }

    private void checkOwner(Long userId) {
        if (userId == null) {
            log.error("Id владельца вещи не указан");
            throw new ValidationException("Id пользователя владельца должен быть указан");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
    }

    private void checkItemExists(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("Вещь в репозитории не найдена");
            throw new NotFoundException(String.format("Вещь с id=%d не найдена", itemId));
        }
    }

    private void checkName(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Имя вещи не указано");
            throw new ValidationException("Имя должно быть указано");
        }
    }

    private void checkDescription(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Описание вещи не указано");
            throw new ValidationException("Описание должно быть указано");
        }
    }

    private void checkAvailable(Item item) {
        if (item.getAvailable() == null) {
            log.error("Доступность вещи не указана");
            throw new ValidationException("Доступность должна быть указана");
        }
    }
}
