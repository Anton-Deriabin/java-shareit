package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.annotation.MyJsonTest;
import ru.practicum.shareit.base.BaseJsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@MyJsonTest
public class UserUpdateDtoTest extends BaseJsonTest {

    @Test
    public void testSerialize() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setName("John Doe Updated");
        userUpdateDto.setEmail("john.doe.updated@example.com");
        String json = objectMapper.writeValueAsString(userUpdateDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe Updated\"");
        assertThat(json).contains("\"email\":\"john.doe.updated@example.com\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe Updated\",\"email\":\"john.doe.updated@example.com\"}";
        UserUpdateDto userUpdateDto = objectMapper.readValue(json, UserUpdateDto.class);
        assertThat(userUpdateDto.getId()).isEqualTo(1L);
        assertThat(userUpdateDto.getName()).isEqualTo("John Doe Updated");
        assertThat(userUpdateDto.getEmail()).isEqualTo("john.doe.updated@example.com");
    }
}
