package ru.yandex.practicum.filmorate.service.exception;

/**
 * Occurs in case if review not found by its identity.
 */
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(long id) {
        super(String.format("Review with id %d not found", id));
    }
}
