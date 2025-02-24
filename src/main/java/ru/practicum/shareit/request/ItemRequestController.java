package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final String requestsIdPath = "/{requestId}";
    private final String allPath = "/all";
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @RequestHeader(value = userIdHeader, required = false) Long requestorId) {
        return itemRequestService.createItemRequest(itemRequestCreateDto, requestorId);
    }

    @GetMapping(requestsIdPath)
    public ItemRequestDto findById(@PathVariable Long requestId) {
        return itemRequestService.findById(requestId);
    }

    @GetMapping()
    public List<ItemRequestDto> findByRequestorId(@RequestHeader(value = userIdHeader, required = false)
                                                      Long requestorId) {
        return itemRequestService.findByRequestorId(requestorId);
    }

    @GetMapping(allPath)
    public List<ItemRequestDto> findAll() {
        return itemRequestService.findAll();
    }
}
