package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CheckUserService checkUserService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requestor;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "John Doe", "john.doe@example.com");
        itemRequestCreateDto = new ItemRequestCreateDto("Need a book", LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "Need a book", LocalDateTime.now(), 1L, null);
        itemRequest = ItemRequestMapper.mapToItemRequestFromCreateDto(itemRequestCreateDto, requestor);
        itemRequest.setId(1L);
        itemRequest.setCreated(itemRequestDto.getCreated());
    }

    @Test
    void testCreateItemRequestWhenRequestIsCreatedThenReturnItemRequestDto() {
        when(checkUserService.checkUser(1L)).thenReturn(requestor);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestCreateDto, 1L);
        assertThat(result).isEqualTo(itemRequestDto);
        verify(checkUserService, times(1)).checkUser(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void testFindByIdWhenRequestExistsThenReturnItemRequestDto() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        ItemRequestDto result = itemRequestService.findById(1L);
        assertThat(result).isEqualTo(itemRequestDto);
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdWhenRequestDoesNotExistThenThrowNotFoundException() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(1L));
        assertThat(exception.getMessage()).isEqualTo("Запрос вещи с id=1 не найден");
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByRequestorIdWhenRequestsExistThenReturnItemRequestDtos() {
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(checkUserService.checkUser(1L)).thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(requests);
        List<ItemRequestDto> result = itemRequestService.findByRequestorId(1L);
        assertThat(result).isEqualTo(requestDtos);
        verify(checkUserService, times(1)).checkUser(1L);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void testFindAllWhenRequestsExistThenReturnItemRequestDtos() {
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(itemRequestRepository.findAll()).thenReturn(requests);
        List<ItemRequestDto> result = itemRequestService.findAll();
        assertThat(result).isEqualTo(requestDtos);
        verify(itemRequestRepository, times(1)).findAll();
    }

    @Test
    void testFindByRequestorIdWhenUserNotFoundThenThrowNotFoundException() {
        when(checkUserService.checkUser(2L)).thenThrow(new NotFoundException("Пользователь с id = 2 не найден"));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.findByRequestorId(2L));
        assertThat(exception.getMessage()).isEqualTo("Пользователь с id = 2 не найден");
        verify(checkUserService, times(1)).checkUser(2L);
    }
}
