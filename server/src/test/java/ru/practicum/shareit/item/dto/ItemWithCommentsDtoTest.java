package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemWithCommentsDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        BookingDto lastBooking = new BookingDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED,
                new UserDto(1L, "User", "user@example.com"), null);
        BookingDto nextBooking = new BookingDto(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED,
                new UserDto(1L, "User", "user@example.com"), null);
        CommentDto commentDto = new CommentDto(1L, "Comment", 1L, 1L, "Author",
                LocalDateTime.now());
        ItemWithCommentsDto itemWithCommentsDto = new ItemWithCommentsDto(1L, "Item Name",
                "Item Description", true, 2L, lastBooking, nextBooking, List.of(commentDto));
        String json = objectMapper.writeValueAsString(itemWithCommentsDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"ownerId\":2");
        assertThat(json).contains("\"lastBooking\"");
        assertThat(json).contains("\"nextBooking\"");
        assertThat(json).contains("\"comments\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true," +
                "\"ownerId\":2,\"lastBooking\":{\"id\":1,\"start\":\"2023-10-08T10:10:10\"," +
                "\"end\":\"2023-10-09T10:10:10\",\"status\":\"APPROVED\",\"booker\":{\"id\":1,\"name\":\"User\"," +
                "\"email\":\"user@example.com\"}},\"nextBooking\":{\"id\":2,\"start\":\"2023-10-11T10:10:10\"," +
                "\"end\":\"2023-10-12T10:10:10\",\"status\":\"APPROVED\",\"booker\":{\"id\":1,\"name\":\"User\"," +
                "\"email\":\"user@example.com\"}},\"comments\":[{\"id\":1,\"text\":\"Comment\",\"itemId\":1," +
                "\"authorId\":1,\"authorName\":\"Author\",\"created\":\"2023-10-10T10:10:10\"}]}";
        ItemWithCommentsDto itemWithCommentsDto = objectMapper.readValue(json, ItemWithCommentsDto.class);
        assertThat(itemWithCommentsDto.getId()).isEqualTo(1L);
        assertThat(itemWithCommentsDto.getName()).isEqualTo("Item Name");
        assertThat(itemWithCommentsDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemWithCommentsDto.getAvailable()).isTrue();
        assertThat(itemWithCommentsDto.getOwnerId()).isEqualTo(2L);
        assertThat(itemWithCommentsDto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(itemWithCommentsDto.getNextBooking().getId()).isEqualTo(2L);
        assertThat(itemWithCommentsDto.getComments()).hasSize(1);
    }
}
