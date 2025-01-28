package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public UserDto create(User user) {
        checkEmail(user);
        checkName(user);
        return UserMapper.mapToUserDto(userRepository.create(user));
    }

    public UserDto update(User newUser, Long id) {
        newUser.setId(id);
        checkUserExists(newUser.getId());
        if (newUser.getEmail() != null) {
            checkEmail(newUser);
        }
        return UserMapper.mapToUserDto(userRepository.update(newUser));
    }

    public UserDto delete(Long id) {
        checkUserExists(id);
        return userRepository.delete(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    private void checkUserExists(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь не найден");
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Имя пользователя не указано");
            throw new ValidationException("Имя должно быть указано");
        }
    }

    private void checkEmail(User user) {
        for (User value : userRepository.users.values()) {
            if (user.getEmail().equals(value.getEmail())) {
                log.error("Попытка занять уже используемый email");
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
    }
}

