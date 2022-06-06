package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewReadModel {
    Collection<Review> getReviewsByFilmId(long id, int count);
}
