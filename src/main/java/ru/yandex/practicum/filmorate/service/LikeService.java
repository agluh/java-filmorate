package ru.yandex.practicum.filmorate.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.events.UserLikedFilm;
import ru.yandex.practicum.filmorate.events.UserRevokedLikeOfFilm;
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

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Marks a film as liked by user.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    @Transactional
    public void doLike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        if (likeStorage.getLikeMetadataByUserAndFilm(userId, filmId).isPresent()) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        Like like = new Like(userId, filmId, now);
        likeStorage.save(like);

        eventPublisher.publishEvent(new UserLikedFilm(ZonedDateTime.now(), userId, filmId));
    }

    /**
     * Removes user's like from the film.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    @Transactional
    public void doUnlike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        Optional<Like> like = likeStorage.getLikeMetadataByUserAndFilm(userId, filmId);

        like.ifPresent(l -> {
            likeStorage.delete(l);
            eventPublisher.publishEvent(new UserRevokedLikeOfFilm(ZonedDateTime.now(),
                userId, filmId));
        });
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
