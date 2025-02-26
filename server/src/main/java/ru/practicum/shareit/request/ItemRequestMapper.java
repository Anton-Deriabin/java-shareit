package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        if (itemRequest.getItems() != null) {
            List<ItemResponseDto> itemResponseDtos = itemRequest.getItems().stream()
                    .map(ItemMapper::mapToItemResponseDto)
                    .toList();
            dto.setItems(itemResponseDtos);
        }
        return dto;
    }

    public static ItemRequest mapToItemRequestFromCreateDto(ItemRequestCreateDto dto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(dto.getCreated());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }
}
