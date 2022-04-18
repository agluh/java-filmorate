package ru.yandex.practicum.filmorate.dao.exceptions;

/**
 * Represents common errors on DAO layer.
 */
public class DaoException extends RuntimeException {

    public DaoException(Throwable cause) {
        super(cause);
    }
}
