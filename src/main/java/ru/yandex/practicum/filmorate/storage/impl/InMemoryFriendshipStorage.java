package ru.yandex.practicum.filmorate.storage.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

/**
 * In memory implementation of friendship storage.
 */
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Set<Friendship>> inviters = new HashMap<>();
    private final Map<Long, Set<Friendship>> acceptors = new HashMap<>();
    private final Map<ComposedKey, Friendship> composedIndex = new HashMap<>();

    @Override
    public void save(Friendship friendship) {
        composedIndex.put(ComposedKey.from(friendship), friendship);

        inviters.putIfAbsent(friendship.getInviterId(), new HashSet<>());
        inviters.get(friendship.getInviterId()).add(friendship);

        acceptors.putIfAbsent(friendship.getAcceptorId(), new HashSet<>());
        acceptors.get(friendship.getAcceptorId()).add(friendship);
    }

    @Override
    public void delete(Friendship friendship) {
        composedIndex.remove(ComposedKey.from(friendship));
        removeIfPresent(inviters, friendship::getInviterId, friendship);
        removeIfPresent(acceptors, friendship::getAcceptorId, friendship);
    }

    private <T> void removeIfPresent(Map<Long, Set<T>> map,
        Supplier<? extends Long> keySupplier, T elem) {
        Long key = keySupplier.get();

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

        Friendship friendship = composedIndex.get(key);
        if (friendship == null) {
            friendship = composedIndex.get(key.getOpposite());
        }

        return Optional.ofNullable(friendship);
    }

    @Override
    public Collection<Friendship> getFriendshipMetadataOfUser(long userId) {
        Set<Friendship> set = inviters.get(userId);
        if (set == null || set.isEmpty()) {
            set = acceptors.get(userId);
        }
        return set != null ? set : Collections.emptySet();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class ComposedKey {
        final long id1;
        final long id2;

        static ComposedKey from(Friendship friendship) {
            return new ComposedKey(friendship.getInviterId(), friendship.getAcceptorId());
        }

        ComposedKey getOpposite() {
            return new ComposedKey(id2, id1);
        }
    }
}
