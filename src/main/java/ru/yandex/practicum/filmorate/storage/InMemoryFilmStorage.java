package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    @Autowired
    @Qualifier("InMemoryUserStorage")
    private final UserStorage storageUser;
    private final Map<Long, Film> films = new HashMap<>();

    public InMemoryFilmStorage(UserStorage storageUser) {
        this.storageUser = storageUser;
    }

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new NotFoundException("Фильм с id = " + id + " не найден");
    }

    @Override
    public Film create(Film film) {
        log.info("Валидация входящего запроса");
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен в коллекцию");
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Валидация входящего запроса");
            validateFilm(newFilm);
            log.info("Обновление параметров фильма");
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильма успешно обновлен");
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма: {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12, 28))) {
            log.warn("Дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = storageUser.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Данный пользователь уже поставил лайк фильму");
        }

        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = storageUser.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Данный пользователь не поставил лайк фильму");
        }

        film.getLikes().remove(userId);
        return film;
    }

    @Override
    public Collection<Film> popularFilms(int count) {
        return findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
