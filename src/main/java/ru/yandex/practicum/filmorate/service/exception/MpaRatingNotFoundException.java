package ru.yandex.practicum.filmorate.service.exception;

/**
 * Occurs in case if MPA rating not found by its identity.
 */
public class MpaRatingNotFoundException extends RuntimeException {

    public MpaRatingNotFoundException(long id) {
        super(String.format("MPA rating with id %d not found", id));
    }
}
