package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingRequestDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        Long bookerId = 2L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId, bookerId);
        String json = objectMapper.writeValueAsString(bookingRequestDto);
        assertThat(json).contains("\"start\"");
        assertThat(json).contains("\"end\"");
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"bookerId\":2");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"start\":\"2023-10-01T10:00:00\",\"end\":\"2023-10-02T10:00:00\",\"itemId\":1,\"bookerId\":2}";
        BookingRequestDto bookingRequestDto = objectMapper.readValue(json, BookingRequestDto.class);
        assertThat(bookingRequestDto.getStart()).isEqualTo(LocalDateTime.parse("2023-10-01T10:00:00"));
        assertThat(bookingRequestDto.getEnd()).isEqualTo(LocalDateTime.parse("2023-10-02T10:00:00"));
        assertThat(bookingRequestDto.getItemId()).isEqualTo(1L);
        assertThat(bookingRequestDto.getBookerId()).isEqualTo(2L);
    }
}
