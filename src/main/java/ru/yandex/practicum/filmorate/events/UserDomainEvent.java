package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Base class for user related domain events.
 */
@Getter
public abstract class UserDomainEvent extends DomainEvent {

    protected final long userId;

    public UserDomainEvent(ZonedDateTime occurredOn, long userId) {
        super(occurredOn);
        this.userId = userId;
    }
}
