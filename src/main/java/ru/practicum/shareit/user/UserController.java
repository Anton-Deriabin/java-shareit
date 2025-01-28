package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

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
    public UserDto create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping(usersIdPath)
    public UserDto update(@Valid @RequestBody User user, @PathVariable Long id) {
        return userService.update(user, id);
    }

    @DeleteMapping(usersIdPath)
    public UserDto delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
