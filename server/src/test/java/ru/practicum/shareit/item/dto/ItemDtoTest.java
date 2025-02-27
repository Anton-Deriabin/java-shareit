package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 2L);

        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"ownerId\":2");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true," +
                "\"ownerId\":2}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwnerId()).isEqualTo(2L);
    }
}
