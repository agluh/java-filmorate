package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Optional<Review> getReview(long id);

    void save(Review review);

    void delete(long reviewId);
}
