package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.base.BaseWebMvcTest;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Arrays;
import java.util.List;

public class UserControllerTest extends BaseWebMvcTest {

    private UserDto userDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("John Doe");
        userCreateDto.setEmail("john.doe@example.com");
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setName("John Doe Updated");
        userUpdateDto.setEmail("john.doe.updated@example.com");
    }

    @Test
    public void testFindAllWhenServiceReturnsUsersThenReturnListOfUsers() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        BDDMockito.given(userService.findAll()).willReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    public void testFindUserWhenServiceReturnsUserThenReturnUser() throws Exception {
        BDDMockito.given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testCreateWhenServiceCreatesUserThenReturnUser() throws Exception {
        BDDMockito.given(userService.create(Mockito.any(UserCreateDto.class))).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testUpdateWhenServiceUpdatesUserThenReturnUser() throws Exception {
        BDDMockito.given(userService.update(Mockito.any(UserUpdateDto.class), Mockito.eq(1L)))
                .willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe Updated\",\"email\":\"john.doe.updated@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testDeleteWhenServiceDeletesUserThenReturnUser() throws Exception {
        BDDMockito.given(userService.delete(1L)).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }
}
