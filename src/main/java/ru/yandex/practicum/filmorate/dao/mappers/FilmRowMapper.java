package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Autowired
    GenreDbStorage genreStorage;
    @Autowired
    MpaDbStorage mpaStorage;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Mpa mpa = mpaStorage.findById((long) resultSet.getInt("mpa_id"));
        film.setMpa(mpa);

        Set<Genre> genresOfFilm = new HashSet<>(genreStorage.getGenresByFilmId(film.getId()));
        film.setGenres(genresOfFilm);

        return film;
    }
}
