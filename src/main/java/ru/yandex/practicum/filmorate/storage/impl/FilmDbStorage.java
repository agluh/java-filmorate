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

    public static final String SELECT_FILM =
        "SELECT film_id, name, description, release_date, duration, mpa"
            + " FROM films WHERE film_id = ?";
    public static final String SELECT_FILMS =
        "SELECT film_id, name, description, release_date, duration, mpa FROM films";
    public static final String SELECT_POPULAR_FILMS =
        "SELECT f.film_id, f.name, f.description, f.release_date,"
            + " f.duration, f.mpa"
            + " FROM films AS f"
            + " LEFT JOIN likes AS l on f.film_id = l.film_id"
            + " GROUP BY f.film_id"
            + " ORDER BY COUNT(DISTINCT l.user_id) DESC"
            + " LIMIT ?";
    public static final String INSERT_FILM =
        "INSERT INTO films (name, description, release_date, duration, mpa) "
            + "VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_FILM =
        "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ?"
        + " WHERE film_id = ?";
    public static final String SELECT_LIKE =
        "SELECT user_id, film_id, created_at FROM likes WHERE user_id = ? AND film_id = ?";
    public static final String UPDATE_LIKE =
        "MERGE INTO likes (user_id, film_id, created_at) KEY (user_id, film_id) VALUES (?, ?, ?)";
    public static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return jdbcTemplate.query(SELECT_FILM, this::mapRowToFilm, id).stream().findAny();
    }

    @Override
    public void save(Film film) {
        if (film.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_FILM,
                    new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setLong(4, film.getDuration());
                ps.setString(5, film.getMpa().name());
                return ps;
            }, keyHolder);

            injectId(film, keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getMpa().name(), film.getId());
        }
    }

    @Override
    public void save(Like like) {
        jdbcTemplate.update(UPDATE_LIKE, like.getUserid(), like.getFilmId(), like.getCreatedAt());
    }

    @Override
    public void delete(Like like) {
        jdbcTemplate.update(DELETE_LIKE, like.getUserid(), like.getFilmId());
    }

    @Override
    public Optional<Like> getLikeMetadataByUserAndFilm(long userId, long filmId) {
        return jdbcTemplate.query(SELECT_LIKE, this::mapRowToLike, userId, filmId)
            .stream().findAny();
    }

    @Override
    public Collection<Film> getAll() {
        return jdbcTemplate.query(SELECT_FILMS, this::mapRowToFilm);
    }

    @Override
    public Collection<Film> getMostPopularFilms(int maxCount) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS, this::mapRowToFilm, maxCount);
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
            MpaRating.valueOf(rs.getString("mpa").trim())
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
