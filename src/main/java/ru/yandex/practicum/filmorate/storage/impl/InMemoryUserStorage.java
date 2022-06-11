package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserReadModel;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * In memory implementation of users repository.
 */
@Component
public class InMemoryUserStorage implements UserStorage, FriendshipStorage, UserReadModel {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Friendship>> left = new HashMap<>();
    private final Map<Long, Set<Friendship>> right = new HashMap<>();
    private final Map<ComposedKey, Friendship> composedIndex = new HashMap<>();

    private static long nextId = 1;

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void save(User user) {
        try {
            if (user.getId() == null) {
                injectId(user);
            }

            users.put(user.getId(), user);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

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
    public Collection<User> getAll() {
        return users.values();
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
    public Collection<User> getFriendsOfUser(long userId) {
        return getFriendshipMetadataOfUser(userId).stream()
            .filter(f -> f.isConfirmed() || f.getInviterId() == userId)
            .map(f -> getUser(getFriendFromFriendship(f, userId)))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public Collection<User> getCommonFriendsOfUsers(long userId, long otherId) {
        return getFriendsOfUser(userId).stream()
            .distinct()
            .filter(getFriendsOfUser(otherId)::contains)
            .collect(Collectors.toSet());
    }

    private void injectId(User user) throws NoSuchFieldException, IllegalAccessException {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, getNextId());
    }

    private long getNextId() {
        return nextId++;
    }

    /**
     * Extracts opposite side of friendship.
     */
    private long getFriendFromFriendship(Friendship friendship, long userId) {
        if (friendship.getInviterId() == userId) {
            return friendship.getAcceptorId();
        }

        return friendship.getInviterId();
    }

    private Collection<Friendship> getFriendshipMetadataOfUser(long userId) {
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
