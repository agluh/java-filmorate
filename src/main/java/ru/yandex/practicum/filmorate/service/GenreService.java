package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreReadModel;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

/**
 * Provides service layer for genres management.
 */
@Service
@AllArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;
    private final GenreReadModel genreReadModel;

    public Collection<Genre> getAllGenres() {
        return genreReadModel.getAll();
    }

    public Genre getGenre(long genreId) {
        return genreStorage.getGenre(genreId).orElseThrow(() ->
            new GenreNotFoundException(genreId));
    }
}
