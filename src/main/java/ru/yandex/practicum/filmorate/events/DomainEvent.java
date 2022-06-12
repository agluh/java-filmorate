package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Base class for domain events.
 */
@Getter
@AllArgsConstructor
public abstract class DomainEvent {

    protected ZonedDateTime occurredOn;
}
