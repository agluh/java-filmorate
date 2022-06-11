package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewLike;

public interface ReviewLikeStorage {
    void save(ReviewLike reviewLike);

    void delete(ReviewLike reviewLike);
}
