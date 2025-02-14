package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckUserService;

import java.util.List;

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CheckUserService checkUserService;

    public List<ItemDto> findAllFromUser(Long userId) {
        checkUserService.checkUser(userId);
        List<ItemDto> userItemDtos = itemRepository.findByOwnerId(userId)
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
        log.info("Получено {} вещей пользователя", userItemDtos.size());
        return userItemDtos;
    }

    public ItemDto findById(Long id) {
        return itemRepository.findById(id)
                .map(item -> {
                    log.info("Вещь c id = {} найдена", id);
                    return ItemMapper.mapToItemDto(item);
                })
                .orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id=%d не найдена", id));
                });
    }

    public List<ItemDto> findByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<ItemDto> itemDtos = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .toList();
        log.info("Получено {} вещей по текстовой подстроке", itemDtos.size());
        return itemDtos;
    }

    @Transactional
    public ItemDto create(ItemCreateDto itemDto, Long userId) {
        User owner = checkUserService.checkUser(userId);
        Item item = ItemMapper.mapToItemFromCreateDto(itemDto, owner);
        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(item)),
                savedItem -> log.info("Вещь с id = {} добавлена", savedItem.getId())
        );
    }

    @Transactional
    public ItemDto update(ItemUpdateDto itemDto, Long userId, Long id) {
        User owner = checkUserService.checkUser(userId);
        ItemDto oldItem = findById(id);
        log.trace("Создали переменную старой вещи для обновления");
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
            log.debug("Вещи с id = {} установлено имя - {}", oldItem.getId(), oldItem.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
            log.debug("Вещи с id = {} установлено описание - {}", oldItem.getId(), oldItem.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
            log.debug("Вещи с id = {} установлена доступность - {}", oldItem.getId(), oldItem.getAvailable());
        }
        log.info("Вещь \"{}\" с id = {} - обновлена в репозитории", oldItem.getName(), oldItem.getId());
        return logAndReturn(
                ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItemFromDto(oldItem, owner))),
                savedItem -> log.info("Вещь \"{}\" с id = {} обновлена", savedItem.getName(), savedItem.getId())
                );
    }
}
