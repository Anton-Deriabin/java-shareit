package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.EmailHolder;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static ru.practicum.shareit.utils.LoggingUtils.logAndReturn;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        List<UserDto> userDtos = userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
        log.info("Получено {} пользователей", userDtos.size());
        return userDtos;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    log.info("Пользователь c id = {} найден", id);
                    return UserMapper.mapToUserDto(user);
                })
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", id);
                    return new NotFoundException(String.format("Пользователь с id=%d не найден", id));
                });
    }

    @Transactional
    public UserDto create(UserCreateDto userDto) {
        checkEmail(userDto);
        return logAndReturn(
                UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUserFromCreateDto(userDto))),
                savedUser -> log.info("Пользователь с id = {} добавлен", savedUser.getId())
        );
    }

    @Transactional
    public UserDto update(UserUpdateDto userDto, Long id) {
        UserDto oldUser = findById(id);
        log.trace("Создали переменную старого пользователя для обновления");

        if (userDto.getEmail() != null) {
            checkEmail(userDto);
            oldUser.setEmail(userDto.getEmail());
            log.debug("Пользователю с id = {} установлен email - {}", id, oldUser.getEmail());
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
            log.debug("Пользователю с id = {} установлено имя - {}", id, oldUser.getName());
        }

        return logAndReturn(
                UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUserFromDto(oldUser))),
                updatedUser -> log.info("Пользователь \"{}\" с id = {} обновлен",
                        updatedUser.getName(), updatedUser.getId())
        );
    }

    @Transactional
    public UserDto delete(Long id) {
        UserDto userDto = findById(id);
        userRepository.delete(UserMapper.mapToUserFromDto(userDto));
        log.info("Пользователь с id = {}  - удален", id);
        return userDto;
    }

    private <T extends EmailHolder> void checkEmail(T userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            log.error("Email {} уже используется", userDto.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }
        log.info("Email {} доступен для использования", userDto.getEmail());
    }
}