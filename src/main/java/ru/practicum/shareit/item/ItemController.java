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
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping()
    public List<ItemDto> findAllFromUser(@RequestHeader(value = userIdHeader, required = false) Long userId) {
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
        return itemService.findByText(text);
    }

    @PostMapping()
    public ItemDto create(@Valid @RequestBody Item item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping(itemsIdPath)
    public ItemDto update(@Valid @RequestBody Item item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId,
                          @PathVariable Long id) {
        return itemService.update(item, userId, id);
    }
}

