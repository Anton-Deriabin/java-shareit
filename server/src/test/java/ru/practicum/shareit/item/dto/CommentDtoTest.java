package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "This is a comment", 1L, 1L,
                "Author", LocalDateTime.now());

        String json = objectMapper.writeValueAsString(commentDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"This is a comment\"");
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"authorId\":1");
        assertThat(json).contains("\"authorName\":\"Author\"");
        assertThat(json).contains("\"created\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"text\":\"This is a comment\",\"itemId\":1,\"authorId\":1,\"authorName\":\"Author\"," +
                "\"created\":\"2023-10-10T10:10:10\"}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("This is a comment");
        assertThat(commentDto.getItemId()).isEqualTo(1L);
        assertThat(commentDto.getAuthorId()).isEqualTo(1L);
        assertThat(commentDto.getAuthorName()).isEqualTo("Author");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-10T10:10:10"));
    }
}
