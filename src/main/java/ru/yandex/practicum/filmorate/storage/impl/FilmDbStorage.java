package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmReadModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/**
 * DB based implementation of film storage.
 */
@Component
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
            + " LEFT JOIN film_genre AS fg on f.film_id = fg.film_id"
            + " WHERE %s"
            + " GROUP BY f.film_id"
            + " ORDER BY COUNT(DISTINCT l.user_id) DESC"
            + " LIMIT ?";

    public static final String SELECT_FILMS_BY_NAME_SUBSTRING =
        "SELECT film_id, name, description, "
            + "release_date, duration, mpa FROM films WHERE LOWER(name) LIKE ?";

    public static final String INSERT_FILM =
        "INSERT INTO films (name, description, release_date, duration, mpa) "
            + "VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_FILM =
        "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ?"
            + " WHERE film_id = ?";

    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    public static final String SELECT_LIKE =
        "SELECT user_id, film_id, created_at FROM likes WHERE user_id = ? AND film_id = ?";
    public static final String UPDATE_LIKE =
        "MERGE INTO likes (user_id, film_id, created_at) KEY (user_id, film_id) VALUES (?, ?, ?)";
    public static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    public static final String SELECT_GENRES =
        "SELECT g.genre_id AS genre_id, g.name AS name FROM film_genre AS fg"
            + " JOIN genres AS g ON fg.genre_id = g.genre_id"
            + " WHERE film_id = ?";
    public static final String DELETE_GENRES =
        "DELETE FROM film_genre WHERE film_id = ?";
    public static final String INSERT_GENRE =
        "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public static final String SELECT_RECOMMENDATIONS = "SELECT * FROM films " +
            "WHERE film_id " +
            "          IN (SELECT DISTINCT l.film_id FROM likes AS l " +
            "                  WHERE l.user_id " +
            "                      IN (SELECT l.user_id FROM likes AS l " +
            "                          WHERE l.film_id " +
            "                              IN (SELECT f.film_id FROM films AS f " +
            "                                        RIGHT JOIN likes AS l ON f.film_id = l.film_id " +
            "                                  WHERE l.user_id = ?)" +
            "                              AND l.user_id <> ?)" +
            "                    AND l.film_id NOT IN (SELECT l.film_id FROM likes AS l WHERE l.user_id = ?));";

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

        jdbcTemplate.update(DELETE_GENRES, film.getId());

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(INSERT_GENRE, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_FILM, id);
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
    public Collection<Film> getMostPopularFilms(int limit) {
        return jdbcTemplate.query(String.format(SELECT_POPULAR_FILMS, "1 = 1"),
            this::mapRowToFilm, limit);
    }

    @Override
    public Collection<Film> getMostPopularFilms(OptionalLong genreId, OptionalInt year, int limit) {
        if (genreId.isPresent() && year.isPresent()) {
            String sql = String.format(SELECT_POPULAR_FILMS,
                "fg.genre_id = ? AND YEAR (f.release_date) = ?");
            return jdbcTemplate.query(sql, this::mapRowToFilm,
                genreId.getAsLong(), year.getAsInt(), limit);
        }

        if (genreId.isPresent()) {
            String sql = String.format(SELECT_POPULAR_FILMS,
                "fg.genre_id = ?");
            return jdbcTemplate.query(sql, this::mapRowToFilm,
                genreId.getAsLong(), limit);
        }

        if (year.isPresent()) {
            String sql = String.format(SELECT_POPULAR_FILMS,
                "YEAR (f.release_date) = ?");
            return jdbcTemplate.query(sql, this::mapRowToFilm,
                year.getAsInt(), limit);
        }

        return getMostPopularFilms(limit);
    }

    /**
     * Performs search by film name.
     *
     * @see <a href="https://stackoverflow.com/questions/55499110">related issue</a>
     */
    @Override
    public Collection<Film> getFilmsBySearch(String query) {
        return jdbcTemplate.query(SELECT_FILMS_BY_NAME_SUBSTRING, this::mapRowToFilm, "%"
            + query.toLowerCase() + "%");
    }

    @Override
    public Collection<Film> getRecommendationsForUser(Long userId) {
        return jdbcTemplate.query(SELECT_RECOMMENDATIONS, this::mapRowToFilm,userId,userId,userId);
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
        long filmId = rs.getLong("film_id");

        List<Genre> genres = jdbcTemplate.query(
            SELECT_GENRES,
            (resultSet, num) -> new Genre(
                resultSet.getLong("genre_id"),
                resultSet.getString("name")
            ), filmId);

        return new Film(
            filmId,
            rs.getString("name"),
            rs.getString("description"),
            rs.getDate("release_date").toLocalDate(),
            rs.getLong("duration"),
            MpaRating.valueOf(rs.getString("mpa").trim()),
            genres.isEmpty() ? null : new HashSet<>(genres)
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
