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
        // Arrange
        when(checkUserService.checkUser(1L)).thenReturn(requestor);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        // Act
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestCreateDto, 1L);

        // Assert
        assertThat(result).isEqualTo(itemRequestDto);
        verify(checkUserService, times(1)).checkUser(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void testFindByIdWhenRequestExistsThenReturnItemRequestDto() {
        // Arrange
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        // Act
        ItemRequestDto result = itemRequestService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(itemRequestDto);
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdWhenRequestDoesNotExistThenThrowNotFoundException() {
        // Arrange
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(1L));
        assertThat(exception.getMessage()).isEqualTo("Запрос вещи с id=1 не найден");
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByRequestorIdWhenRequestsExistThenReturnItemRequestDtos() {
        // Arrange
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(checkUserService.checkUser(1L)).thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(requests);

        // Act
        List<ItemRequestDto> result = itemRequestService.findByRequestorId(1L);

        // Assert
        assertThat(result).isEqualTo(requestDtos);
        verify(checkUserService, times(1)).checkUser(1L);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void testFindAllWhenRequestsExistThenReturnItemRequestDtos() {
        // Arrange
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(itemRequestRepository.findAll()).thenReturn(requests);

        // Act
        List<ItemRequestDto> result = itemRequestService.findAll();

        // Assert
        assertThat(result).isEqualTo(requestDtos);
        verify(itemRequestRepository, times(1)).findAll();
    }
}
