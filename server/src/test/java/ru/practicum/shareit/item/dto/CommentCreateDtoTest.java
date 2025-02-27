package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentCreateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("This is a comment");

        String json = objectMapper.writeValueAsString(commentCreateDto);
        assertThat(json).contains("\"text\":\"This is a comment\"");
        assertThat(json).contains("\"created\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"text\":\"This is a comment\",\"created\":\"2023-10-10T10:10:10\"}";

        CommentCreateDto commentCreateDto = objectMapper.readValue(json, CommentCreateDto.class);
        assertThat(commentCreateDto.getText()).isEqualTo("This is a comment");
        assertThat(commentCreateDto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-10T10:10:10"));
    }
}
