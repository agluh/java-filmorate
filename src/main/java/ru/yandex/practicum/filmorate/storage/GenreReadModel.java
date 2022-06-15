package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Interface for genre read model.
 */
public interface GenreReadModel {

    Collection<Genre> getAll();
}
