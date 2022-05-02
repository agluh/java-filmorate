package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * In memory implementation of films repository.
 */
@Service
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    private static long nextId = 1;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void save(Film film) {
        try {
            if (film.getId() == null) {
                injectId(film);
            }

            films.put(film.getId(), film);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    private long getNextId() {
        return nextId++;
    }

    private void injectId(Film film) throws NoSuchFieldException, IllegalAccessException {
        Field idField = Film.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(film, getNextId());
    }
}
