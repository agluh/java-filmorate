package ru.yandex.practicum.filmorate.storage.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

/**
 * In memory implementation of friendship storage.
 */
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Set<Friendship>> left = new HashMap<>();
    private final Map<Long, Set<Friendship>> right = new HashMap<>();
    private final Map<ComposedKey, Friendship> composedIndex = new HashMap<>();

    @Override
    public void save(Friendship friendship) {
        ComposedKey key = ComposedKey.from(friendship);
        composedIndex.put(key, friendship);

        left.putIfAbsent(key.id1, new HashSet<>());
        left.get(key.id1).add(friendship);

        right.putIfAbsent(key.id2, new HashSet<>());
        right.get(key.id2).add(friendship);
    }

    @Override
    public void delete(Friendship friendship) {
        ComposedKey key = ComposedKey.from(friendship);
        composedIndex.remove(key);
        removeIfPresent(left, key.id1, friendship);
        removeIfPresent(right, key.id2, friendship);
    }

    private <T> void removeIfPresent(Map<Long, Set<T>> map,
        long key, T elem) {

        if (map.containsKey(key)) {
            map.get(key).remove(elem);

            if (map.get(key).isEmpty()) {
                map.remove(key);
            }
        }
    }

    @Override
    public Optional<Friendship> getFriendshipMetadataByUserIds(long userId, long otherId) {
        ComposedKey key = new ComposedKey(userId, otherId);
        return Optional.ofNullable(composedIndex.get(key));
    }

    @Override
    public Collection<Friendship> getFriendshipMetadataOfUser(long userId) {
        Set<Friendship> set = new HashSet<>();

        set.addAll(left.getOrDefault(userId, Collections.emptySet()));
        set.addAll(right.getOrDefault(userId, Collections.emptySet()));

        return set;
    }

    @EqualsAndHashCode
    private static class ComposedKey {
        final long id1;
        final long id2;

        private ComposedKey(long id1, long id2) {
            this.id1 = Math.min(id1, id2);
            this.id2 = Math.max(id1, id2);
        }

        static ComposedKey from(Friendship friendship) {
            return new ComposedKey(friendship.getInviterId(), friendship.getAcceptorId());
        }
    }
}
