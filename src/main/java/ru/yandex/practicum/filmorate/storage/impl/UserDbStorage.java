package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserReadModel;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * DB based implementation of user storage.
 */
@Repository
public class UserDbStorage implements UserStorage, FriendshipStorage, UserReadModel {

    public static final String SELECT_USER =
        "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
    public static final String SELECT_USERS =
        "SELECT user_id, email, login, name, birthday FROM users";
    public static final String INSERT_USER =
        "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_USER =
        "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    private static final String DELETE_USER =
            "DELETE FROM users WHERE user_id = ?";
    public static final String SELECT_FRIENDSHIP =
        "SELECT inviter_id, acceptor_id, is_confirmed FROM friendship"
            + " WHERE (inviter_id = ? AND acceptor_id = ?)"
            + " OR (acceptor_id = ? AND inviter_id = ?)";
    public static final String UPDATE_FRIENDSHIP =
        "MERGE INTO friendship (inviter_id, acceptor_id, is_confirmed)"
            + " KEY (inviter_id, acceptor_id) VALUES (?, ?, ?)";
    public static final String DELETE_FRIENDSHIP =
        "DELETE FROM friendship WHERE inviter_id = ? AND acceptor_id = ?";
    public static final String SELECT_FRIENDS =
        "SELECT user_id, email, login, name, birthday"
        + " FROM users AS u"
        + " WHERE u.user_id IN ("
        + "   (SELECT acceptor_id AS user_id FROM friendship WHERE inviter_id = ?)"
        + "   UNION"
        + "   (SELECT inviter_id AS user_id FROM friendship WHERE acceptor_id = ?"
        + "      AND is_confirmed IS TRUE)"
        + " )";
    public static final String SELECT_COMMON_FRIENDS =
        "SELECT user_id, email, login, name, birthday"
            + " FROM users AS u"
            + " WHERE u.user_id IN ("
            + "   (SELECT acceptor_id AS user_id FROM friendship WHERE inviter_id = ?)"
            + "   UNION"
            + "   (SELECT inviter_id AS user_id FROM friendship WHERE acceptor_id = ?"
            + "      AND is_confirmed IS TRUE)"
            + " )"
            + "AND u.user_id IN ("
            + "   (SELECT acceptor_id AS user_id FROM friendship WHERE inviter_id = ?)"
            + "   UNION"
            + "   (SELECT inviter_id AS user_id FROM friendship WHERE acceptor_id = ?"
            + "      AND is_confirmed IS TRUE)"
            + " )";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUser(long id) {
        return jdbcTemplate.query(SELECT_USER, this::mapRowToUser, id).stream().findAny();
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_USER,
                    new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            injectId(user, keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getLogin(),
                user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        }
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public void save(Friendship friendship) {
        jdbcTemplate.update(UPDATE_FRIENDSHIP, friendship.getInviterId(),
                friendship.getAcceptorId(), friendship.isConfirmed());
    }

    @Override
    public void delete(Friendship friendship) {
        jdbcTemplate.update(DELETE_FRIENDSHIP, friendship.getInviterId(),
                friendship.getAcceptorId());
    }

    @Override
    public Optional<Friendship> getFriendshipMetadataByUserIds(long userId, long otherId) {
        return jdbcTemplate.query(SELECT_FRIENDSHIP, this::mapRowToFriendship,
                userId, otherId, userId, otherId).stream().findAny();
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SELECT_USERS, this::mapRowToUser);
    }

    @Override
    public Collection<User> getFriendsOfUser(long userId) {
        return jdbcTemplate.query(SELECT_FRIENDS, this::mapRowToUser, userId, userId);
    }

    @Override
    public Collection<User> getCommonFriendsOfUsers(long userId, long otherId) {
        return jdbcTemplate.query(SELECT_COMMON_FRIENDS, this::mapRowToUser,
                userId, userId, otherId, otherId);
    }

    private void injectId(User user, long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    private Friendship mapRowToFriendship(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(
                rs.getLong("inviter_id"),
                rs.getLong("acceptor_id"),
                rs.getBoolean("is_confirmed")
        );
    }
}
