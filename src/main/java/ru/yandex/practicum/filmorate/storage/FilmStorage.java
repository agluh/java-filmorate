package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Repository for films.
 */
public interface FilmStorage {

    Optional<Film> getFilm(long id);

    void save(Film film);
}
