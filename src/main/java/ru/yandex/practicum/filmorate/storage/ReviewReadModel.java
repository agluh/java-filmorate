package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewReadModel {
    Collection<Review> getReviewsByFilmId(long id, int count);
}
