package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId);

    ItemRequestDto findById(Long requestId);

    List<ItemRequestDto> findByRequestorId(Long requestorId);

    List<ItemRequestDto> findAll();
}
