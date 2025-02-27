package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.annotation.MyJsonTest;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@MyJsonTest
public class UserCreateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Jane Doe");
        userCreateDto.setEmail("jane.doe@example.com");

        String json = objectMapper.writeValueAsString(userCreateDto);
        assertThat(json).contains("\"name\":\"Jane Doe\"");
        assertThat(json).contains("\"email\":\"jane.doe@example.com\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}";

        UserCreateDto userCreateDto = objectMapper.readValue(json, UserCreateDto.class);
        assertThat(userCreateDto.getName()).isEqualTo("Jane Doe");
        assertThat(userCreateDto.getEmail()).isEqualTo("jane.doe@example.com");
    }
}
