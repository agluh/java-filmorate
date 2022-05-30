package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserReadModel;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Provides service layer for users management.
 */
@Service
public class UserService {
    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    private final UserReadModel userReadModel;

    @Autowired
    public UserService(
        UserStorage userStorage,
        FriendshipStorage friendshipStorage,
        UserReadModel userReadModel
    ) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.userReadModel = userReadModel;
    }

    /**
     * Creates a new user.
     */
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        userStorage.save(user);
        return user;
    }

    /**
     * Updates a new user.
     */
    public User updateUser(User user) {
        long id = user.getId();
        User existedUser = userStorage.getUser(id).orElseThrow(() ->
            new UserNotFoundException(id));

        existedUser.setName(user.getName());
        existedUser.setEmail(user.getEmail());
        existedUser.setLogin(user.getLogin());
        existedUser.setBirthday(user.getBirthday());

        userStorage.save(existedUser);
        return existedUser;
    }

    public Collection<User> getAllUsers() {
        return userReadModel.getAll();
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId).orElseThrow(() ->
            new UserNotFoundException(userId));
    }

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
            if (existedFriendship.isInitiatedBy(inviterId) || existedFriendship.isConfirmed()) {
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
    public Collection<User> getFriendsOfUser(long userId) {
        ensureUserExists(userId);

        return userReadModel.getFriendsOfUser(userId);
    }

    /**
     * Returns list of common friends of two users.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public Collection<User> getCommonFriends(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        return userReadModel.getCommonFriendsOfUsers(userId, otherId);
    }

    private void ensureUserExists(long userId) {
        userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
