package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemUpdateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setId(1L);
        itemUpdateDto.setName("Updated Item Name");
        itemUpdateDto.setDescription("Updated Item Description");
        itemUpdateDto.setAvailable(false);

        String json = objectMapper.writeValueAsString(itemUpdateDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Updated Item Name\"");
        assertThat(json).contains("\"description\":\"Updated Item Description\"");
        assertThat(json).contains("\"available\":false");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Updated Item Name\",\"description\":\"Updated Item Description\"," +
                "\"available\":false}";

        ItemUpdateDto itemUpdateDto = objectMapper.readValue(json, ItemUpdateDto.class);
        assertThat(itemUpdateDto.getId()).isEqualTo(1L);
        assertThat(itemUpdateDto.getName()).isEqualTo("Updated Item Name");
        assertThat(itemUpdateDto.getDescription()).isEqualTo("Updated Item Description");
        assertThat(itemUpdateDto.getAvailable()).isFalse();
    }
}
