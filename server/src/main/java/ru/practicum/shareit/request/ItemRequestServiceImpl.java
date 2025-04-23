package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckUserService;

import java.util.Comparator;
import java.util.List;

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final CheckUserService checkUserService;

    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestCreateDto dto, String requestorId) {
        User requestor = checkUserService.checkUser(requestorId);
        ItemRequest saved = itemRequestRepository.save(
                ItemRequestMapper.mapToItemRequestFromCreateDto(dto, requestor)
        );
        return logAndReturn(
                ItemRequestMapper.mapToItemRequestDto(saved),
                ir -> log.info("Запрос с id = {} создан", ir.getId())
        );
    }

    public ItemRequestDto findById(String requestId) {
        return itemRequestRepository.findById(requestId)
                .map(ItemRequestMapper::mapToItemRequestDto)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
    }

    public List<ItemRequestDto> findByRequestorId(String requestorId) {
        checkUserService.checkUser(requestorId);
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    public List<ItemRequestDto> findAll() {
        return itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }
}
