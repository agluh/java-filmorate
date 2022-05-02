package ru.yandex.practicum.filmorate.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Provides friendship management.
 */
@Service
@AllArgsConstructor
public class FriendshipService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    /**
     * Makes two users friend of each other.
     *
     * @return true if users were not friends before, false otherwise.
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public boolean makeFriends(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        if (friendshipStorage.getFriendshipMetadataByUserIds(userId, otherId).isPresent()) {
            return false;
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        Friendship friendship = new Friendship(userId, otherId, now);
        friendshipStorage.save(friendship);
        return true;
    }

    /**
     * Breaks friendship between two users.
     *
     * @return true if users were friends before, false otherwise.
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public boolean unfriendUsers(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        Optional<Friendship> friendship =
            friendshipStorage.getFriendshipMetadataByUserIds(userId, otherId);

        boolean wasFriends = friendship.isPresent();
        friendship.ifPresent(friendshipStorage::delete);

        return wasFriends;
    }

    /**
     * Returns list of friends of user.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public Set<User> getFriendsOfUser(long userId) {
        ensureUserExists(userId);

        // TODO: could be better if we retrieve all users from user storage at once
        return friendshipStorage.getFriendshipMetadataOfUser(userId).stream()
            .map(f -> userStorage.getUser(getFriendFromFriendship(f, userId)))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Returns list of common friends of two users.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public Set<User> getCommonFriends(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        // TODO: maybe it is better to achieve this on a storage layer
        return getFriendsOfUser(userId).stream()
            .distinct()
            .filter(getFriendsOfUser(otherId)::contains)
            .collect(Collectors.toSet());
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

    private void ensureUserExists(long userId) {
        userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
