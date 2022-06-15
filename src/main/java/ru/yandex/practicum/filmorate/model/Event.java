package ru.yandex.practicum.filmorate.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Event model.
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @EqualsAndHashCode.Include
    private final Long eventId;

    private final long userId;

    private final long entityId;

    private final ZonedDateTime occurredOn;

    private final String eventType;

    private final String operation;
}
