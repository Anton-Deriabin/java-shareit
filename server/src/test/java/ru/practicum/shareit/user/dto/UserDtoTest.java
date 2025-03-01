package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.annotation.MyJsonTest;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@MyJsonTest
public class UserDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
    }
}
