package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Provides service layer for users management.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    /**
     * Creates a new user.
     */
    public User createUser(User user) {
        if (user.getName() == null) {
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
        return userStorage.getAll();
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId).orElseThrow(() ->
            new UserNotFoundException(userId));
    }
}
