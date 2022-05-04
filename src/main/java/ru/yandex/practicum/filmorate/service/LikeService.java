package ru.yandex.practicum.filmorate.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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
     * @return true if film was not liked by user before, false otherwise.
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    public boolean doLike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        if (likeStorage.getLikeMetadataByUserAndFilm(userId, filmId).isPresent()) {
            return false;
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        Like like = new Like(userId, filmId, now);
        likeStorage.save(like);
        return true;
    }

    /**
     * Removes user's like from the film.
     *
     * @return true if film was previously liked by user, false otherwise.
     * @throws UserNotFoundException in case user not found by its identity.
     * @throws FilmNotFoundException in case film not found by its identity.
     */
    public boolean doUnlike(long userId, long filmId) {
        ensureUserExists(userId);
        ensureFilmExists(filmId);

        Optional<Like> like = likeStorage.getLikeMetadataByUserAndFilm(userId, filmId);

        boolean wasLiked = like.isPresent();
        like.ifPresent(likeStorage::delete);

        return wasLiked;
    }

    /**
     * Retrieves specified count of most popular films.
     */
    public Collection<Film> getMostPopularFilms(int maxCount) {
        @AllArgsConstructor
        @Getter
        class RatedFilm implements Comparable<RatedFilm> {
            Film film;
            int rate;

            @Override
            public int compareTo(RatedFilm o) {
                return Integer.compare(rate, o.rate);
            }
        }

        Collection<Long> mostLikedFilms = likeStorage.getMostLikedFilms(maxCount);

        return filmStorage.getAll().stream()
            .map(film -> new RatedFilm(film, mostLikedFilms.contains(film.getId()) ? 1 : 0))
            .sorted(Comparator.comparingInt(RatedFilm::getRate).reversed())
            .map(RatedFilm::getFilm)
            .limit(maxCount)
            .collect(Collectors.toList());
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
