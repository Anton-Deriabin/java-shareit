package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemWithBookingsCommentsDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                Status.APPROVED, new UserDto(1L, "User", "user@example.com"), null);
        CommentDto commentDto = new CommentDto(1L, "Comment", 1L, 1L, "Author",
                LocalDateTime.now());
        ItemWithBookingsCommentsDto itemWithBookingsCommentsDto = new ItemWithBookingsCommentsDto(1L,
                "Item Name", "Item Description", true, 2L, List.of(bookingDto),
                List.of(commentDto));
        String json = objectMapper.writeValueAsString(itemWithBookingsCommentsDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"ownerId\":2");
        assertThat(json).contains("\"bookings\"");
        assertThat(json).contains("\"comments\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true," +
                "\"ownerId\":2,\"bookings\":[{\"id\":1,\"start\":\"2023-10-10T10:10:10\"," +
                "\"end\":\"2023-10-11T10:10:10\",\"status\":\"APPROVED\",\"booker\":{\"id\":1,\"name\":\"User\"," +
                "\"email\":\"user@example.com\"}}],\"comments\":[{\"id\":1,\"text\":\"Comment\",\"itemId\":1," +
                "\"authorId\":1,\"authorName\":\"Author\",\"created\":\"2023-10-10T10:10:10\"}]}";
        ItemWithBookingsCommentsDto itemWithBookingsCommentsDto = objectMapper.readValue(json, ItemWithBookingsCommentsDto.class);
        assertThat(itemWithBookingsCommentsDto.getId()).isEqualTo(1L);
        assertThat(itemWithBookingsCommentsDto.getName()).isEqualTo("Item Name");
        assertThat(itemWithBookingsCommentsDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemWithBookingsCommentsDto.getAvailable()).isTrue();
        assertThat(itemWithBookingsCommentsDto.getOwnerId()).isEqualTo(2L);
        assertThat(itemWithBookingsCommentsDto.getBookings()).hasSize(1);
        assertThat(itemWithBookingsCommentsDto.getComments()).hasSize(1);
    }
}
