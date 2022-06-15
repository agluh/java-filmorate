package ru.yandex.practicum.filmorate.service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.events.UserAddedFriend;
import ru.yandex.practicum.filmorate.events.UserRemovedFriend;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.EventReadModel;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserReadModel;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Provides service layer for users management.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final UserReadModel userReadModel;
    private final EventReadModel eventReadModel;
    private final ApplicationEventPublisher eventPublisher;

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
    @Transactional
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
            eventPublisher.publishEvent(new UserAddedFriend(ZonedDateTime.now(),
                acceptorId, inviterId));
            return;
        }

        Friendship friendship = new Friendship(inviterId, acceptorId, false);
        friendshipStorage.save(friendship);
        eventPublisher.publishEvent(new UserAddedFriend(ZonedDateTime.now(),
            inviterId, acceptorId));
    }

    /**
     * Breaks friendship between two users.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    @Transactional
    public void unfriendUsers(long userId, long otherId) {
        ensureUserExists(userId);
        ensureUserExists(otherId);

        Optional<Friendship> friendship =
            friendshipStorage.getFriendshipMetadataByUserIds(userId, otherId);

        friendship.ifPresent(f -> {
            friendshipStorage.delete(f);
            eventPublisher.publishEvent(new UserRemovedFriend(ZonedDateTime.now(),
                userId, otherId));
        });
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

    /**
     * Returns list of last events user related to.
     *
     * @throws UserNotFoundException in case user not found by its identity.
     */
    public Collection<Event> getEventsOfUser(long userId) {
        ensureUserExists(userId);
        return eventReadModel.getEventsListForUser(userId);
    }

    private void ensureUserExists(long userId) {
        userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void deleteUser(long userId) {
        ensureUserExists(userId);
        userStorage.delete(userId);
    }
}
