package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class ItemRepository {
    @Getter
    protected final Map<Long, Item> items = new HashMap<>();

    public Collection<Item> findAllFromUser() {
        log.info("Коллекция вещей отправлена по запросу");
        return items.values();
    }

    public Optional<Item> findById(Long id) {
        log.info("Вещь отправлена по запросу");
        return Optional.ofNullable(items.get(id));
    }

    public Item create(Item item) {
        checkName(item);
        checkDescription(item);
        checkAvailable(item);
        item.setId(getNextId());
        log.debug("Вещи \"{}\" назначен id = {}", item.getName(), item.getId());
        items.put(item.getId(), item);
        log.info("Вещь с id = {}  - добавлена", item.getId());
        return item;
    }

    public Item update(Item newItem) {
        checkItemExists(newItem.getId());
        Item oldItem = items.get(newItem.getId());
        log.trace("Создали переменную старой вещи для обновления");
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
            log.debug("Вещи с id = {} установлено имя - {}", newItem.getId(), newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
            log.debug("Вещи с id = {} установлено описание - {}", newItem.getId(), newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
            log.debug("Вещи с id = {} установлена доступность - {}", newItem.getId(), newItem.getAvailable());
        }
        log.info("Вещь \"{}\" с id = {} - обновлена", newItem.getName(), newItem.getId());
        return oldItem;
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream().mapToLong(id -> id).max().orElse(0);
        log.debug("Cоздали новый id = {} ", currentMaxId);
        return ++currentMaxId;
    }

    private void checkItemExists(long itemId) {
        if (findById(itemId).isEmpty()) {
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
