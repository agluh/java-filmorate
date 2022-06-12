package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewReadModel;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

@Repository
public class ReviewDbStorage implements ReviewStorage, ReviewReadModel, ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_REVIEW =
        "SELECT r.review_id, r.user_id, r.film_id,r.is_positive,r.content,"
            + " CASE WHEN SUM(rate.rate) IS NULL THEN 0 ELSE SUM(rate.rate) END AS useful"
            + " FROM reviews AS r"
            + " LEFT JOIN (SELECT COUNT(user_id) AS rate, review_id"
            + "           FROM review_likes"
            + "           WHERE is_useful = TRUE"
            + "           GROUP BY review_id"
            + "           UNION ALL"
            + "           SELECT -1*COUNT(user_id) AS rate, review_id"
            + "           FROM review_likes"
            + "           WHERE is_useful = FALSE"
            + "           GROUP BY review_id) AS rate ON r.review_id = rate.review_id"
            + " WHERE r.review_id = ?"
            + " GROUP BY r.review_id";

    private static final String SELECT_REVIEWS_BY_FILM =
        "SELECT r.review_id, r.user_id, r.film_id,r.is_positive,r.content,"
            + " CASE WHEN SUM(rate.rate) IS NULL THEN 0 ELSE SUM(rate.rate) END AS useful"
            + " FROM reviews AS r"
            + " LEFT JOIN (SELECT COUNT(user_id) AS rate, review_id"
            + "           FROM review_likes"
            + "           WHERE is_useful = TRUE"
            + "           GROUP BY review_id"
            + "           UNION ALL"
            + "           SELECT -1*COUNT(user_id) as rate, review_id"
            + "           FROM review_likes"
            + "           WHERE is_useful = FALSE"
            + "           GROUP BY REVIEW_ID) AS rate ON r.review_id = rate.review_id"
            + " WHERE r.film_id = ?"
            + " GROUP BY r.review_id"
            + " ORDER BY useful DESC"
            + " LIMIT ?";

    private static final String INSERT_REVIEW =
        "INSERT INTO reviews (user_id, film_id, is_positive, content) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_REVIEW =
        "UPDATE reviews SET user_id = ?, film_id = ?,"
            + " is_positive = ?, content = ? WHERE review_id = ?";

    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_LIKE =
            "MERGE INTO review_likes (review_id, user_id, is_useful) KEY (review_id, user_id)"
                + " VALUES (?, ?, ?)";

    private static final String DELETE_REVIEW_LIKE =
            "DELETE FROM review_likes WHERE REVIEW_ID = ? AND USER_ID = ?";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Review> getReview(long id) {
        return jdbcTemplate.query(SELECT_REVIEW, this::mapRowToReview, id).stream().findFirst();
    }

    @Override
    public void save(Review review) {
        if (review.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_REVIEW,
                        new String[]{"review_id"});
                ps.setLong(1, review.getUserId());
                ps.setLong(2, review.getFilmId());
                ps.setBoolean(3, review.isPositive());
                ps.setString(4, review.getContent());
                return ps;
            }, keyHolder);

            injectId(review, keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE_REVIEW, review.getUserId(), review.getFilmId(),
                review.isPositive(), review.getContent(), review.getId());
        }
    }

    @Override
    public void save(ReviewLike reviewLike) {
        jdbcTemplate.update(UPDATE_REVIEW_LIKE, reviewLike.getReviewId(), reviewLike.getUserId(),
            reviewLike.isUseful());
    }

    @Override
    public void delete(ReviewLike reviewLike) {
        jdbcTemplate.update(DELETE_REVIEW_LIKE, reviewLike.getReviewId(), reviewLike.getUserId());
    }

    @Override
    public void delete(long reviewId) {
        jdbcTemplate.update(DELETE_REVIEW, reviewId);
    }

    @Override
    public Collection<Review> getReviewsByFilmId(long filmId, int count) {
        return jdbcTemplate.query(SELECT_REVIEWS_BY_FILM, this::mapRowToReview, filmId, count);
    }

    private void injectId(Review review, long id) {
        try {
            Field idField = Review.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(review, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getLong("review_id"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getBoolean("is_positive"),
                rs.getString("content"),
                rs.getInt("useful")
        );
    }
}
