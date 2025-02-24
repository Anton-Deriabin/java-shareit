package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String itemsIdPath = "/{id}";
    private final String searchPath = "/search";
    private final String commentPath = "/{itemId}/comment";
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping()
    public List<ItemWithBookingsCommentsDto> findAllFromUser(
            @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return itemService.findAllFromUser(userId);
    }

    @GetMapping(itemsIdPath)
    public ItemWithCommentsDto findItem(@PathVariable Long id,
                                        @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return itemService.findById(id, userId);
    }

    @GetMapping(searchPath)
    public List<ItemDto> findItemByText(@RequestParam(required = false) String text) {
        return itemService.findByText(text);
    }

    @PostMapping()
    public ItemDto create(@Valid @RequestBody ItemCreateDto item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping(itemsIdPath)
    public ItemDto update(@RequestBody ItemUpdateDto item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId,
                          @PathVariable Long id) {
        return itemService.update(item, userId, id);
    }

    @PostMapping(commentPath)
    public CommentDto createComment(@Valid @RequestBody CommentCreateDto comment,
                                    @RequestHeader(value = userIdHeader, required = false) Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(comment, userId, itemId);
    }
}