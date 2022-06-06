package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user revoked a like of the film.
 */
@Getter
public final class UserRevokedLikeOfFilm extends UserDomainEvent {

    private final long filmId;

    public UserRevokedLikeOfFilm(ZonedDateTime occurredOn, long userId, long filmId) {
        super(occurredOn, userId);
        this.filmId = filmId;
    }
}
