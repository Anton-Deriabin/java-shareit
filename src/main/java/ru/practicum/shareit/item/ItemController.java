package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String itemsIdPath = "/{id}";
    private final String searchPath = "/search";
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping()
    public List<ItemDto> findAllFromUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя владельца должен быть указан");
        }
        userService.findById(userId);
        return itemService.findAllFromUser(userId);
    }

    @GetMapping(itemsIdPath)
    public ItemDto findItem(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping(searchPath)
    public List<ItemDto> findItemByText(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemService.findByText(text);
    }

    @PostMapping()
    public ItemDto create(@Valid @RequestBody Item item,
                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя владельца должен быть указан");
        }
        userService.findById(userId);
        item.setOwner(userId);
        return itemService.create(item);
    }

    @PatchMapping(itemsIdPath)
    public ItemDto update(@Valid @RequestBody Item item,
                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                          @PathVariable Long id) {
        if (userId == null) {
            throw new ValidationException("Id пользователя владельца должен быть указан");
        }
        userService.findById(userId);
        item.setOwner(userId);
        item.setId(id);
        return itemService.update(item);
    }
}

