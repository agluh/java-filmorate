package ru.yandex.practicum.filmorate.service.exception;

/**
 * Occurs in case if film not found by its identity.
 */
public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(long id) {
        super(String.format("Film with id %d not found", id));
    }
}
