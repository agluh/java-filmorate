package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreReadModel;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

/**
 * DB based implementation of genre storage.
 */
@Component
public class GenreDbStorage implements GenreStorage, GenreReadModel {

    private static final String SELECT_GENRE =
        "SELECT genre_id, name FROM genres WHERE genre_id = ?";
    private static final String SELECT_GENRES =
        "SELECT genre_id, name FROM genres";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Genre> getGenre(long id) {
        return jdbcTemplate.query(SELECT_GENRE, this::mapRowToGenre, id).stream().findAny();
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query(SELECT_GENRES, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
            rs.getLong("genre_id"),
            rs.getString("name")
        );
    }
}
