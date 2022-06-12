package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Event;

/**
 * Read model for events.
 */
public interface EventReadModel {

    Collection<Event> getEventsListForUser(long userId);
}
