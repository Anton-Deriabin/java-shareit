package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemServiceImplIntegrationTest extends BaseSpringBootTest {

    private Item item;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentCreateDto commentCreateDto;
    private User user;

    @BeforeEach
    public void setUp() {
        // Создаем и сохраняем пользователя
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        // Создаем и сохраняем предмет
        item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        // Создаем и сохраняем бронирование
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        // Инициализируем DTO для создания предмета
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item2");
        itemCreateDto.setDescription("Description2");
        itemCreateDto.setAvailable(true);

        // Инициализируем DTO для обновления предмета
        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Item1 Updated");
        itemUpdateDto.setDescription("Description1 Updated");
        itemUpdateDto.setAvailable(false);

        // Инициализируем DTO для создания комментария
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great item!");
    }

    @Test
    public void testFindAllFromUserWhenItemsExistThenReturnListOfItems() {
        List<ItemWithBookingsCommentsDto> items = itemService.findAllFromUser(user.getId());
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo(item.getName());
        assertThat(items.getFirst().getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testFindByIdWhenItemExistsThenReturnItem() {
        ItemWithCommentsDto foundItem = itemService.findById(item.getId(), user.getId());
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo(item.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testFindByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.findById(999L, user.getId()));
    }

    @Test
    public void testFindByTextWhenItemsExistThenReturnListOfItems() {
        List<ItemDto> items = itemService.findByText("Item1");
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo(item.getName());
        assertThat(items.getFirst().getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testCreateWhenItemIsValidThenReturnCreatedItem() {
        ItemDto createdItem = itemService.create(itemCreateDto, user.getId());
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(createdItem.getDescription()).isEqualTo(itemCreateDto.getDescription());
    }

    @Test
    public void testUpdateWhenItemIsValidThenReturnUpdatedItem() {
        ItemDto updatedItem = itemService.update(itemUpdateDto, user.getId(), item.getId());
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo(itemUpdateDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(itemUpdateDto.getDescription());
    }

    @Test
    public void testUpdateWhenItemDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.update(itemUpdateDto, user.getId(), 999L));
    }

    @Test
    public void testCreateCommentWhenCommentIsValidThenReturnCreatedComment() {
        CommentDto createdComment = itemService.createComment(commentCreateDto, user.getId(), item.getId());
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getText()).isEqualTo(commentCreateDto.getText());
    }
}