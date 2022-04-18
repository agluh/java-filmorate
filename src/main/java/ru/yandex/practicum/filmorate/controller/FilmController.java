package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

/**
 * Controller for films.
 */
@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Adds a new film.
     */
    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public Film addFilm(@Valid @RequestBody Film film) {
        Film newFilm = filmService.createFilm(film);
        log.info("Add a new film {}", newFilm);
        return newFilm;
    }

    /**
     * Updates an existing film.
     */
    @PutMapping
    @Validated(ValidationMarker.OnUpdate.class)
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            Film updatedFilm = filmService.updateFilm(film);
            log.info("Update film {}", updatedFilm);
            return updatedFilm;
        } catch (FilmNotFoundException e) {
            throw new EntityNotFoundException(e);
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }
}
