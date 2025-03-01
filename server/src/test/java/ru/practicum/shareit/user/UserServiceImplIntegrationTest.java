package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplIntegrationTest extends BaseSpringBootTest {

    private User user;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        user = new User(null, "John Doe", "john.doe@example.com");
        userRepository.save(user);
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Jane Doe");
        userCreateDto.setEmail("jane.doe@example.com");
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(user.getId());
        userUpdateDto.setName("John Doe Updated");
        userUpdateDto.setEmail("john.doe.updated@example.com");
    }

    @Test
    public void testFindAllWhenUsersExistThenReturnListOfUsers() {
        List<UserDto> users = userService.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getName()).isEqualTo(user.getName());
        assertThat(users.getFirst().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindByIdWhenUserExistsThenReturnUser() {
        UserDto foundUser = userService.findById(user.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindByIdWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    public void testCreateWhenUserIsValidThenReturnCreatedUser() {
        UserDto createdUser = userService.create(userCreateDto);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(userCreateDto.getName());
        assertThat(createdUser.getEmail()).isEqualTo(userCreateDto.getEmail());
    }

    @Test
    public void testCreateWhenEmailIsDuplicatedThenThrowDuplicatedDataException() {
        userCreateDto.setEmail(user.getEmail());
        assertThrows(DuplicatedDataException.class, () -> userService.create(userCreateDto));
    }

    @Test
    public void testUpdateWhenUserIsValidThenReturnUpdatedUser() {
        UserDto updatedUser = userService.update(userUpdateDto, user.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }

    @Test
    public void testUpdateWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.update(userUpdateDto, 999L));
    }

    @Test
    public void testDeleteWhenUserExistsThenReturnDeletedUser() {
        UserDto deletedUser = userService.delete(user.getId());
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.getName()).isEqualTo(user.getName());
        assertThat(deletedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testDeleteWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }
}
