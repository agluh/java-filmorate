package ru.yandex.practicum.filmorate.service.exception;

/**
 * Occurs in case if user not found by its identity.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }
}
