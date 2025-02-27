package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemCreateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item Name");
        itemCreateDto.setDescription("Item Description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(1L);

        String json = objectMapper.writeValueAsString(itemCreateDto);
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":1");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true," +
                "\"requestId\":1}";

        ItemCreateDto itemCreateDto = objectMapper.readValue(json, ItemCreateDto.class);
        assertThat(itemCreateDto.getName()).isEqualTo("Item Name");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemCreateDto.getAvailable()).isTrue();
        assertThat(itemCreateDto.getRequestId()).isEqualTo(1L);
    }
}
