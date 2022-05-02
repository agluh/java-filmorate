package ru.yandex.practicum.filmorate.storage.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

/**
 * In memory implementation of like storage.
 */
@Component
public class InMemoryLikeStorage implements LikeStorage {

    private final Map<ComposedKey, Like> index = new HashMap<>();
    private final Map<Long, Integer> filmPopularity = new HashMap<>();

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

    @Override
    public Collection<Long> getMostLikedFilms(int max) {
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
}
