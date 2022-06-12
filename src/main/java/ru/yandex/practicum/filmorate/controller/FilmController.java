package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Controller for films.
 */
@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final LikeService likeService;

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
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Update film {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") long filmId) {
        return filmService.getFilm(filmId);
    }

    /**
     * Makes user likes a film.
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("User {} likes a film {}", userId, filmId);
        likeService.doLike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Makes user unlike a film.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable("id") long filmId,
        @PathVariable long userId) {
        log.info("User {} unlikes a film {}", userId, filmId);
        likeService.doUnlike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int limit,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getMostPopularFilms(genreId, year, limit);
    }

    @GetMapping("/user/{id}/recommendations")
    public Collection<Film> getRecommendationsForUser(@PathVariable("id") Long userId) {
        return filmService.getRecommendations(userId);
    }


    @GetMapping("/search")
    public Collection<Film> getFilmsBySearch(
            @RequestParam String query, @RequestParam String by) {
        return filmService.getFilmsBySearch(query, by);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable("id") long filmId) {
        filmService.deleteFilm(filmId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}