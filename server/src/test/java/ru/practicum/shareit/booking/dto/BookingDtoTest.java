package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        UserDto booker = new UserDto(1L, "Booker Name", "booker@example.com");
        ItemDto item = new ItemDto(1L, "Item Name", "Item Description", true, 1L);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(1L, start, end, Status.APPROVED, booker, item);
        String json = objectMapper.writeValueAsString(bookingDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\"");
        assertThat(json).contains("\"end\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"booker\":{\"id\":1,\"name\":\"Booker Name\",\"email\":\"booker@example.com\"}");
        assertThat(json).contains("\"item\":{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\"," +
                "\"available\":true,\"ownerId\":1}");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"start\":\"2023-10-01T10:00:00\",\"end\":\"2023-10-02T10:00:00\"," +
                "\"status\":\"APPROVED\",\"booker\":{\"id\":1,\"name\":\"Booker Name\"," +
                "\"email\":\"booker@example.com\"},\"item\":{\"id\":1,\"name\":\"Item Name\"," +
                "\"description\":\"Item Description\",\"available\":true,\"ownerId\":1}}";
        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.parse("2023-10-01T10:00:00"));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.parse("2023-10-02T10:00:00"));
        assertThat(bookingDto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("Booker Name");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("booker@example.com");
        assertThat(bookingDto.getItem().getId()).isEqualTo(1L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("Item Name");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("Item Description");
        assertThat(bookingDto.getItem().getAvailable()).isTrue();
    }
}
