package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        log.info("Коллекция пользователей получена в репозитории");
        return users.values();
    }

    public Optional<User> findById(Long id) {
        log.info("Пользователь получен в репозитории");
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        user.setId(getNextId());
        log.debug("Пользователю \"{}\" назначен id = {}", user.getName(), user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {}  - добавлен в репозиторий", user.getId());
        return user;
    }

    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        log.trace("Создали переменную старого пользователя для обновления");
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
            log.debug("Пользователю с id = {} установлен email - {}", newUser.getId(), newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
            log.debug("Пользователю с id = {} установлено имя - {}", newUser.getId(), newUser.getName());
        }
        log.info("Пользователь \"{}\" с id = {}  - обновлен в репозитории", newUser.getName(), newUser.getId());
        return oldUser;
    }

    public Optional<User> delete(Long id) {
        Optional<User> userToDelete = findById(id);
        users.remove(id);
        return userToDelete;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        log.debug("Cоздали новый id пользователя = {} ", currentMaxId);
        return ++currentMaxId;
    }
}
