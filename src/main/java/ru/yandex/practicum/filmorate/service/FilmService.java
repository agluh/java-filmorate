package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmReadModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

/**
 * Provides service layer for films management.
 */
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmReadModel filmReadModel;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmReadModel filmReadModel) {
        this.filmStorage = filmStorage;
        this.filmReadModel = filmReadModel;
    }

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
        existedFilm.setMpa(film.getMpa());

        filmStorage.save(existedFilm);
        return existedFilm;
    }

    public Collection<Film> getAllFilms() {
        return filmReadModel.getAll();
    }

    public Collection<Film> getMostPopularFilms(int maxCount) {
        return filmReadModel.getMostPopularFilms(maxCount);
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() ->
            new FilmNotFoundException(filmId));
    }
}
