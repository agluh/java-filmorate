package ru.yandex.practicum.filmorate.service;

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
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public void makeFriends(long inviterId, long acceptorId) {
        ensureUserExists(inviterId);
        ensureUserExists(acceptorId);

        Friendship existedFriendship =
            friendshipStorage.getFriendshipMetadataByUserIds(inviterId, acceptorId)
                .orElse(null);

        if (existedFriendship != null) {
            if (existedFriendship.getInviterId() == inviterId || existedFriendship.isConfirmed()) {
                return;
            }

            existedFriendship.setConfirmed(true);
            friendshipStorage.save(existedFriendship);
            return;
        }

        Friendship friendship = new Friendship(inviterId, acceptorId, false);
        friendshipStorage.save(friendship);
    }

    /**
     * Breaks friendship between two users.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public void unfriendUsers(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        Optional<Friendship> friendship =
            friendshipStorage.getFriendshipMetadataByUserIds(userId, otherId);

        friendship.ifPresent(friendshipStorage::delete);
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
            .filter(f -> f.isConfirmed() || f.getInviterId() == userId)
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
