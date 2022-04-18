package ru.yandex.practicum.filmorate.controller.exception;

/**
 * Occurs in case if entity was not found.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
