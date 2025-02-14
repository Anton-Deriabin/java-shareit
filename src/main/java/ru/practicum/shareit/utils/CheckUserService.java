package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckUserService {
    private final UserRepository userRepository;

    public User checkUser(Long userId) {
        if (userId == null) {
            log.error("Id пользователя не указан, userId = {}", userId);
            throw new ValidationException(String.format("Id пользователя не указан, userId = %d", userId));
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }
}
