package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Repository for users.
 */
public interface UserStorage {

    Collection<User> getAll();

    Optional<User> getUser(long id);

    void save(User user);
}
