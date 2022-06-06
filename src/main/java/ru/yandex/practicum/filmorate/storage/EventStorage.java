package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Event;

/**
 * Repository for events.
 */
public interface EventStorage {

    Optional<Event> getEvent(long id);

    void save(Event event);
}
