package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmReadModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * DB based implementation of film storage.
 */
@Component
@Primary
public class FilmDbStorage implements FilmStorage, LikeStorage, FilmReadModel {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        String sql = "SELECT film_id, name, description, release_date, duration, mpaa"
            + " FROM film WHERE film_id = ?";
        return jdbcTemplate.query(
            sql, this::mapRowToFilm, id
        ).stream().findAny();
    }

    @Override
    public void save(Film film) {
        if (film.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO film (name, description, release_date, duration, mpaa) "
                + "VALUES (?, ?, ?, ?, ?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setLong(4, film.getDuration());
                ps.setString(5, film.getMpa().name());
                return ps;
            }, keyHolder);

            injectId(film, keyHolder.getKey().longValue());
        } else {
            String sql = "UPDATE film SET name = ?, description = ?,"
                + " release_date = ?, duration = ?, mpaa = ?"
                + " WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getMpa().name(), film.getId());
        }
    }

    @Override
    public void save(Like like) {
        String sql = "MERGE INTO `like` (user_id, film_id, created_at)"
            + " KEY (user_id, film_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, like.getUserid(), like.getFilmId(), like.getCreatedAt());
    }

    @Override
    public void delete(Like like) {
        String sql = "DELETE FROM `like` WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, like.getUserid(), like.getFilmId());
    }

    @Override
    public Optional<Like> getLikeMetadataByUserAndFilm(long userId, long filmId) {
        String sql = "SELECT user_id, film_id, created_at FROM `like`"
            + " WHERE user_id = ? AND film_id = ?";
        return jdbcTemplate.query(
            sql, this::mapRowToLike,
            userId, filmId
        ).stream().findAny();
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT film_id, name, description, release_date, duration, mpaa"
            + " FROM film";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Collection<Film> getMostPopularFilms(int maxCount) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date,"
            + " f.duration, f.mpaa"
            + " FROM film AS f"
            + " LEFT JOIN `like` AS l on f.film_id = l.film_id"
            + " GROUP BY f.film_id"
            + " ORDER BY COUNT(DISTINCT l.user_id) DESC"
            + " LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, maxCount);
    }

    private void injectId(Film film, long id) {
        try {
            Field idField = Film.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(film, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
            rs.getLong("film_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDate("release_date").toLocalDate(),
            rs.getLong("duration"),
            MpaRating.valueOf(rs.getString("mpaa"))
        );
    }

    private Like mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        return new Like(
            rs.getLong("user_id"),
            rs.getLong("film_id"),
            rs.getObject("created_at", Instant.class).atZone(ZoneId.of("UTC"))
        );
    }
}
