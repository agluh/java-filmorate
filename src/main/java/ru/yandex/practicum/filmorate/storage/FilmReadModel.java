package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Interface for film read model.
 */
public interface FilmReadModel {
    Collection<Film> getAll();

    Collection<Film> getMostPopularFilms(int maxCount);

    Collection<Film> getRecommendationsForUser(Long userId);
}
