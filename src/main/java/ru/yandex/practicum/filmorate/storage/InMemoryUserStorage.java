package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException("Пользователь с id = " + id + " не найден");
    }

    @Override
    public User create(User user) {
        log.info("Валидация входящего запроса");
        validateUser(user);
        user.setId(getNextId());
        log.info("Создан идентификатор пользователя: {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не заполнено, поэтому присваиваем ему значение логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно сохранен");
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.info("Валидация входящего запроса");
            validateUser(newUser);
            log.info("Обновление данных пользователя");
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Данные пользователя успешно обновлены");
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            log.warn("Email пользователя: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин пользователя: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public Collection<User> addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (user.getFriendIds().contains(friendId)) {
            throw new ValidationException("Данные пользователя уже являются друзьями");
        }

        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);

        return List.of(user, friend);
    }

    public Collection<User> deleteFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);

        return List.of(user, friend);
    }

    public Collection<User> findFriends(Long userId) {
        User user = findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return user.getFriendIds().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (other == null) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }

        return user.getFriendIds().stream()
                .filter(id -> other.getFriendIds().contains(id))
                .map(this::findById)
                .collect(Collectors.toList());
    }

}
