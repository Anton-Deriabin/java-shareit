package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(String id);

    UserDto create(UserCreateDto userDto);

    UserDto update(UserUpdateDto userDto, String id);

    UserDto delete(String id);
}
