package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.OptionalLong;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Interface for film read model.
 */
public interface FilmReadModel {

    Collection<Film> getAll();

    Collection<Film> getMostPopularFilms(int limit);

    default Collection<Film> getMostPopularFilms(
            OptionalLong genreId,
            OptionalInt year,
            int limit) {
        throw new RuntimeException("not implemented");
    }

    Collection<Film> getFilmsBySearch(String query);
}