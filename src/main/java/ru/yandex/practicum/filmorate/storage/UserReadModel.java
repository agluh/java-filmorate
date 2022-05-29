package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Interface for user read model.
 */
public interface UserReadModel {
    Collection<User> getAll();

    Collection<User> getFriendsOfUser(long userId);

    Collection<User> getCommonFriendsOfUsers(long userId, long otherId);
}
