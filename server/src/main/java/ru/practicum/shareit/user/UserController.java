package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final String usersIdPath = "/{id}";
    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping(usersIdPath)
    public UserDto findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping()
    public UserDto create(@RequestBody UserCreateDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(usersIdPath)
    public UserDto update(@RequestBody UserUpdateDto userDto, @PathVariable Long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping(usersIdPath)
    public UserDto delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
