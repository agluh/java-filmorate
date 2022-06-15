package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Repository for genres.
 */
public interface GenreStorage {

    Optional<Genre> getGenre(long genreId);
}
