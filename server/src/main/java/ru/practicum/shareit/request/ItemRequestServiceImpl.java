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
    public ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor  = checkUserService.checkUser(requestorId);
        return logAndReturn(
                ItemRequestMapper.mapToItemRequestDto(itemRequestRepository
                        .save(ItemRequestMapper.mapToItemRequestFromCreateDto(itemRequestCreateDto, requestor))),
        savedItemRequest -> log.info("Запрос вещи с id = {} добавлен", savedItemRequest.getId())
        );
    }

    public ItemRequestDto findById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .map(itemRequest -> {
                    log.info("Запрос вещи c id = {} найден", requestId);
                    return ItemRequestMapper.mapToItemRequestDto(itemRequest);
                })
                .orElseThrow(() -> {
                    log.warn("Запрос вещи с id = {} не найден", requestId);
                    return new NotFoundException(String.format("Запрос вещи с id=%d не найден", requestId));
                });
    }

    public List<ItemRequestDto> findByRequestorId(Long requestorId) {
        checkUserService.checkUser(requestorId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
        log.info("Получено {} запросов пользователя c id = {}", itemRequestDtos.size(), requestorId);
        return itemRequestDtos;
    }

    public List<ItemRequestDto> findAll() {
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAll()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
        log.info("Получено {} запросов вещей", itemRequestDtos.size());
        return itemRequestDtos;
    }
}
