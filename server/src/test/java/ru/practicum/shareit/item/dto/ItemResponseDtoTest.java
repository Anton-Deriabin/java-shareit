package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemResponseDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "Item Name", 2L);
        String json = objectMapper.writeValueAsString(itemResponseDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"ownerId\":2");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"ownerId\":2}";
        ItemResponseDto itemResponseDto = objectMapper.readValue(json, ItemResponseDto.class);
        assertThat(itemResponseDto.getId()).isEqualTo(1L);
        assertThat(itemResponseDto.getName()).isEqualTo("Item Name");
        assertThat(itemResponseDto.getOwnerId()).isEqualTo(2L);
    }
}
