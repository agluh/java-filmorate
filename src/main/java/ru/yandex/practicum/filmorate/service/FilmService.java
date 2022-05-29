package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

/**
 * Provides service layer for films management.
 */
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        filmStorage.save(film);
        return film;
    }

    /**
     * Updates an existing film.
     */
    public Film updateFilm(Film film) {
        long id = film.getId();
        Film existedFilm = filmStorage.getFilm(id).orElseThrow(() ->
            new FilmNotFoundException(id));

        existedFilm.setName(film.getName());
        existedFilm.setDescription(film.getDescription());
        existedFilm.setReleaseDate(film.getReleaseDate());
        existedFilm.setDuration(film.getDuration());
        existedFilm.setMpaa(film.getMpaa());

        filmStorage.save(existedFilm);
        return existedFilm;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() ->
            new FilmNotFoundException(filmId));
    }
}
