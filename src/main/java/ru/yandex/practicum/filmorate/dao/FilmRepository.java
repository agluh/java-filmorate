package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Repository for films.
 */
public interface FilmRepository {

    Collection<Film> getAll();

    Optional<Film> getFilm(long id);

    void save(Film film);
}
