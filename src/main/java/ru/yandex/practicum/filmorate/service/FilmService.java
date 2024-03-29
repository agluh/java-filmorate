package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.OptionalLong;
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

    public static final String SEARCH_BY_TITLE = "TITLE";

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
        existedFilm.setGenres(film.getGenres());

        filmStorage.save(existedFilm);
        return existedFilm;
    }

    public Collection<Film> getRecommendations(Long userId) {
        return filmReadModel.getRecommendationsForUser(userId);
    }

    public Collection<Film> getAllFilms() {
        return filmReadModel.getAll();
    }

    public Collection<Film> getMostPopularFilms(Long genreId, Integer year, int limit) {
        return filmReadModel.getMostPopularFilms(
            genreId != null ? OptionalLong.of(genreId) : OptionalLong.empty(),
            year != null ? OptionalInt.of(year) : OptionalInt.empty(),
            limit
        );
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() ->
            new FilmNotFoundException(filmId));
    }

    public Collection<Film> getFilmsBySearch(String query, String by) {
        if (query != null && by.equalsIgnoreCase(SEARCH_BY_TITLE)) {
            return filmReadModel.getFilmsBySearch(query);
        } else {
            return filmReadModel.getMostPopularFilms(Integer.MAX_VALUE);
        }
    }

    private void ensureFilmExists(long filmId) {
        filmStorage.getFilm(filmId).orElseThrow(() ->
                new FilmNotFoundException(filmId));
    }

    public void deleteFilm(long filmId) {
        ensureFilmExists(filmId);
        filmStorage.delete(filmId);
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return filmReadModel.getCommonFilms(userId, friendId);
    }
}
