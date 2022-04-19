package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;

/**
 * Provides service layer for users management.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user.
     */
    public User createUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        userRepository.save(user);
        return user;
    }

    /**
     * Updates a new user.
     */
    public User updateUser(User user) {
        long id = user.getId();
        User existedUser = userRepository.getUser(id).orElseThrow(() ->
            new UserNotFoundException(id));

        existedUser.setName(user.getName());
        existedUser.setEmail(user.getEmail());
        existedUser.setLogin(user.getLogin());
        existedUser.setBirthday(user.getBirthday());

        userRepository.save(existedUser);
        return existedUser;
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAll();
    }
}
