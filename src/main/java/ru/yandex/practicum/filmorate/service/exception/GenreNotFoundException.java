package ru.yandex.practicum.filmorate.service.exception;

/**
 * Occurs in case if genre not found by its identity.
 */
public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(long id) {
        super(String.format("Genre with id %d not found", id));
    }
}
