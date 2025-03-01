package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final String requestsIdPath = "/{requestId}";
    private final String allPath = "/all";
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping(allPath)
    public ResponseEntity<Object> findAll() {
        return itemRequestClient.findAll();
    }

    @GetMapping(requestsIdPath)
    public ResponseEntity<Object> findById(@PathVariable Long requestId) {
        return itemRequestClient.findById(requestId);
    }

    @GetMapping()
    public ResponseEntity<Object> findByRequestorId(@RequestHeader(value = userIdHeader, required = false)
                                                    Long requestorId) {
        return itemRequestClient.findByRequestorId(requestorId);
    }

    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                    @RequestHeader(value = userIdHeader, required = false) Long requestorId) {
        return itemRequestClient.createItemRequest(itemRequestCreateDto, requestorId);
    }
}
