package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        log.debug("Коллекция получена в репозитории");
        return items.values();
    }

    public Optional<Item> findById(Long id) {
        log.debug("Вещь получена в репозитории");
        return Optional.ofNullable(items.get(id));
    }

    public Collection<Item> findByText(String text) {
        log.debug("Поиск вещей по тексту: {}", text);
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                       (item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText)))
                .toList();
    }

    public Item create(Item item) {
        item.setId(getNextId());
        log.debug("Вещи \"{}\" назначен id = {}", item.getName(), item.getId());
        items.put(item.getId(), item);
        log.info("Вещь с id = {}  - добавлена в репозиторий", item.getId());
        return item;
    }

    public Item update(Item newItem) {
        Item oldItem = items.get(newItem.getId());
        log.trace("Создали переменную старой вещи для обновления в репозитории");
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
        log.info("Вещь \"{}\" с id = {} - обновлена в репозитории", newItem.getName(), newItem.getId());
        return oldItem;
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream().mapToLong(id -> id).max().orElse(0);
        log.debug("Cоздали новый id вещи = {} ", currentMaxId);
        return ++currentMaxId;
    }
}
