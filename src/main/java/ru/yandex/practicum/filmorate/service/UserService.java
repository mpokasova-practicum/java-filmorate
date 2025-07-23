package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(Long id) {
        return storage.findById(id);
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public Collection<User> addFriend(Long userId, Long friendId) {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (user.getFriends().contains(friendId)) {
            throw new ValidationException("Данные пользователя уже являются друзьями");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return List.of(user, friend);
    }

    public Collection<User> deleteFriend(Long userId, Long friendId) {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (!user.getFriends().contains(friendId)) {
            throw new ValidationException("Данные пользователя не являются друзьями");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return List.of(user, friend);
    }

    public Collection<User> findFriends(Long userId) {
        User user = storage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return user.getFriends().stream()
                .map(storage::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        User user = storage.findById(userId);
        User other = storage.findById(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (other == null) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }

        return user.getFriends().stream()
                .filter(id -> other.getFriends().contains(id))
                .map(storage::findById)
                .collect(Collectors.toList());
    }

}
