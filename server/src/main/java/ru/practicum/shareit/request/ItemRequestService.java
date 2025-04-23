package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, String requestorId);

    ItemRequestDto findById(String requestId);

    List<ItemRequestDto> findByRequestorId(String requestorId);

    List<ItemRequestDto> findAll();
}
