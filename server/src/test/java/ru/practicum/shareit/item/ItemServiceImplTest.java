package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.CheckItemService;
import ru.practicum.shareit.utils.CheckUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CheckUserService checkUserService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CheckItemService checkItemService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, user, null);
        itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 1L);
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item Name");
        itemCreateDto.setDescription("Item Description");
        itemCreateDto.setAvailable(true);
        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item Name");
        itemUpdateDto.setDescription("Updated Item Description");
        itemUpdateDto.setAvailable(false);
        comment = new Comment(1L, "Comment text", item, user, LocalDateTime.now());
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Comment text");
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, user,
                Status.APPROVED);
    }

    @Test
    void testFindAllFromUserWhenItemsExistThenReturnItemDtos() {
        // Arrange
        when(checkUserService.checkUser(1L)).thenReturn(user);
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findBookingsByOwnerId(1L)).thenReturn(List.of(booking));
        when(commentRepository.findCommentsByOwnerId(1L)).thenReturn(List.of(comment));

        // Act
        List<ItemWithBookingsCommentsDto> result = itemService.findAllFromUser(1L);

        // Assert
        assertThat(result).hasSize(1);
        verify(itemRepository, times(1)).findByOwnerId(1L);
        verify(bookingRepository, times(1)).findBookingsByOwnerId(1L);
        verify(commentRepository, times(1)).findCommentsByOwnerId(1L);
    }

    @Test
    void testFindByIdWhenItemExistsThenReturnItemDto() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItemId(1L)).thenReturn(List.of(comment));
        when(bookingRepository.findByItemOwner_IdAndStatusOrderByStartDesc(1L, Status.APPROVED))
                .thenReturn(List.of(booking));

        // Act
        ItemWithCommentsDto result = itemService.findById(1L, 1L);

        // Assert
        assertThat(result.getId()).isEqualTo(itemDto.getId());
        verify(itemRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findCommentsByItemId(1L);
    }

    @Test
    void testFindByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.findById(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id=1 не найдена");
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateWhenItemIsCreatedThenReturnItemDto() {
        // Arrange
        when(checkUserService.checkUser(1L)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Act
        ItemDto result = itemService.create(itemCreateDto, 1L);

        // Assert
        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateWhenItemIsUpdatedThenReturnUpdatedItemDto() {
        // Arrange
        when(checkUserService.checkUser(1L)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setName(itemUpdateDto.getName());
            savedItem.setDescription(itemUpdateDto.getDescription());
            savedItem.setAvailable(itemUpdateDto.getAvailable());
            return savedItem;
        });

        // Act
        ItemDto result = itemService.update(itemUpdateDto, 1L, 1L);

        // Assert
        assertThat(result.getName()).isEqualTo(itemUpdateDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemUpdateDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemUpdateDto.getAvailable());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testCreateCommentWhenCommentIsCreatedThenReturnCommentDto() {
        // Arrange
        when(checkUserService.checkUser(1L)).thenReturn(user);
        when(checkItemService.checkItem(1L)).thenReturn(item);
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findByBookerIdWithItem(1L)).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentDto result = itemService.createComment(commentCreateDto, 1L, 1L);

        // Assert
        assertThat(result.getText()).isEqualTo(commentCreateDto.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
