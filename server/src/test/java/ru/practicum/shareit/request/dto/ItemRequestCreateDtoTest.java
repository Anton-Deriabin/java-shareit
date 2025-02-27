package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestCreateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Описание запроса", LocalDateTime.now());
        String json = objectMapper.writeValueAsString(requestDto);
        assertThat(json).contains("\"description\":\"Описание запроса\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"description\":\"Описание запроса\",\"created\":\"2023-10-01T12:00:00\"}";
        ItemRequestCreateDto requestDto = objectMapper.readValue(json, ItemRequestCreateDto.class);
        assertThat(requestDto.getDescription()).isEqualTo("Описание запроса");
    }
}
