package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmReadModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * In memory implementation of films repository.
 */
@Component
public class InMemoryFilmStorage implements FilmStorage, LikeStorage, FilmReadModel {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<ComposedKey, Like> index = new HashMap<>();
    private final Map<Long, Integer> filmPopularity = new HashMap<>();

    private static long nextId = 1;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void save(Film film) {
        try {
            if (film.getId() == null) {
                injectId(film);
            }

            films.put(film.getId(), film);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void save(Like like) {
        index.put(ComposedKey.from(like), like);
        filmPopularity.merge(like.getFilmId(), 1, Integer::sum);
    }

    @Override
    public void delete(Like like) {
        index.remove(ComposedKey.from(like));
        filmPopularity.merge(like.getFilmId(), -1, Integer::sum);
    }

    @Override
    public Optional<Like> getLikeMetadataByUserAndFilm(long userId, long filmId) {
        ComposedKey key = new ComposedKey(userId, filmId);
        return Optional.ofNullable(index.get(key));
    }

    /**
     * Retrieves specified count of most popular films.
     */
    @Override
    public Collection<Film> getMostPopularFilms(int maxCount) {
        Collection<Long> mostLikedFilms = getMostLikedFilms(maxCount);

        return getAll().stream()
            .map(film -> new RatedFilm(film, mostLikedFilms.contains(film.getId()) ? 1 : 0))
            .sorted(Comparator.comparingInt(RatedFilm::getRate).reversed())
            .map(RatedFilm::getFilm)
            .limit(maxCount)
            .collect(Collectors.toList());
    }

    private void injectId(Film film) throws NoSuchFieldException, IllegalAccessException {
        Field idField = Film.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(film, getNextId());
    }

    private long getNextId() {
        return nextId++;
    }

    private Collection<Long> getMostLikedFilms(int max) {
        return filmPopularity.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Entry::getKey)
            .collect(Collectors.toList());
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class ComposedKey {
        final long userId;
        final long filmId;

        static ComposedKey from(Like like) {
            return new ComposedKey(like.getUserid(), like.getFilmId());
        }
    }

    @AllArgsConstructor
    @Getter
    private static class RatedFilm implements Comparable<RatedFilm> {
        Film film;
        int rate;

        @Override
        public int compareTo(RatedFilm o) {
            return Integer.compare(rate, o.rate);
        }
    }
}
