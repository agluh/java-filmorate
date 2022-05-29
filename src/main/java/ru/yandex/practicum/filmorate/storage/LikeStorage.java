package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Like;

/**
 * Repository for likes.
 */
public interface LikeStorage {

    void save(Like like);

    void delete(Like like);

    Optional<Like> getLikeMetadataByUserAndFilm(long userId, long filmId);
}
