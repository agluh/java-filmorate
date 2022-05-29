package ru.yandex.practicum.filmorate.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Provides likes management.
 */
@Service
@AllArgsConstructor
public class LikeService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    /**
     * Marks a film as liked by user.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    public void doLike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        if (likeStorage.getLikeMetadataByUserAndFilm(userId, filmId).isPresent()) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        Like like = new Like(userId, filmId, now);
        likeStorage.save(like);
    }

    /**
     * Removes user's like from the film.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    public void doUnlike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        Optional<Like> like = likeStorage.getLikeMetadataByUserAndFilm(userId, filmId);

        like.ifPresent(likeStorage::delete);
    }

    private void ensureUserExists(long userId) {
        userStorage.getUser(userId).orElseThrow(() ->
            new UserNotFoundException(userId));
    }

    private void ensureFilmExists(long filmId) {
        filmStorage.getFilm(filmId).orElseThrow(() ->
            new FilmNotFoundException(filmId));
    }
}
