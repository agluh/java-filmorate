package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> getReview(long id);

    void save(Review review);

    void delete(long reviewId);
}
