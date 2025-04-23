package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestCreateDto dto,
                                 @RequestHeader(userIdHeader) String requestorId) {
        return itemRequestService.createItemRequest(dto, requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable String requestId) {
        return itemRequestService.findById(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequestorId(@RequestHeader(userIdHeader) String requestorId) {
        return itemRequestService.findByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll() {
        return itemRequestService.findAll();
    }
}
