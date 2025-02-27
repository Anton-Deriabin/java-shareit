package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Описание запроса", LocalDateTime.now(),
                1L, Collections.emptyList());
        String json = objectMapper.writeValueAsString(requestDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Описание запроса\"");
        assertThat(json).contains("\"requestorId\":1");
        assertThat(json).contains("\"items\":[]");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"description\":\"Описание запроса\",\"created\":\"2023-10-01T12:00:00\"," +
                "\"requestorId\":1,\"items\":[]}";
        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);
        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Описание запроса");
        assertThat(requestDto.getRequestorId()).isEqualTo(1L);
        assertThat(requestDto.getItems()).isEmpty();
    }
}
