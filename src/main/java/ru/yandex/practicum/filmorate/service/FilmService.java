package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;

/**
 * Provides service layer for films management.
 */
@Service
public class FilmService {
    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public Film createFilm(Film film) {
        filmRepository.save(film);
        return film;
    }

    /**
     * Updates an existing film.
     */
    public Film updateFilm(Film film) {
        long id = film.getId();
        Film existedFilm = filmRepository.getFilm(id).orElseThrow(() ->
            new FilmNotFoundException(id));

        existedFilm.setName(film.getName());
        existedFilm.setDescription(film.getDescription());
        existedFilm.setReleaseDate(film.getReleaseDate());
        existedFilm.setDuration(film.getDuration());

        filmRepository.save(existedFilm);
        return existedFilm;
    }

    public Collection<Film> getAllFilms() {
        return filmRepository.getAll();
    }
}
