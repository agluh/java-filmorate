package ru.yandex.practicum.filmorate.dao.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dao.exceptions.DaoException;
import ru.yandex.practicum.filmorate.model.User;

/**
 * In memory implementation of users repository.
 */
@Service
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long nextId = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

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

    private long getNextId() {
        return nextId++;
    }

    private void injectId(User user) throws NoSuchFieldException, IllegalAccessException {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, getNextId());
    }
}
