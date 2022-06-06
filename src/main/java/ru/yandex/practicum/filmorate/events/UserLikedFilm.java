package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user liked a film.
 */
@Getter
public final class UserLikedFilm extends UserDomainEvent {

    private final long filmId;

    public UserLikedFilm(ZonedDateTime occurredOn, long userId, long filmId) {
        super(occurredOn, userId);
        this.filmId = filmId;
    }
}
