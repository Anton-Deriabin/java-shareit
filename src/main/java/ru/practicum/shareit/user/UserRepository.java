package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserRepository {
    @Getter
    protected final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Коллекция пользователей отправлена по запросу");
        return users.values();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        checkEmail(user);
        checkName(user);
        user.setId(getNextId());
        log.debug("Пользователю \"{}\" назначен id = {}", user.getName(), user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {}  - добавлен", user.getId());
        return user;
    }

    public User update(User newUser) {
        checkUserExists(newUser.getId());
        User oldUser = users.get(newUser.getId());
        log.trace("Создали переменную старого пользователя для обновления");
        if (newUser.getEmail() != null) {
            checkEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
            log.debug("Пользователю с id = {} установлен email - {}", newUser.getId(), newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
            log.debug("Пользователю с id = {} установлено имя - {}", newUser.getId(), newUser.getName());
        }
        log.info("Пользователь \"{}\" с id = {}  - обновлен", newUser.getName(), newUser.getId());
        return oldUser;
    }

    public Optional<User> delete(Long id) {
        checkUserExists(id);
        Optional<User> userToDelete = findById(id);
        users.remove(id);
        return userToDelete;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        log.debug("Cоздали новый id = {} ", currentMaxId);
        return ++currentMaxId;
    }

    private void checkUserExists(long userId) {
        if (findById(userId).isEmpty()) {
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
        for (User value : users.values()) {
            if (user.getEmail().equals(value.getEmail())) {
                log.error("Попытка занять уже используемый email");
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
    }
}
