package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserReadModel;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * DB based implementation of user storage.
 */
@Component
@Primary
public class UserDbStorage implements UserStorage, FriendshipStorage, UserReadModel {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUser(long id) {
        String sql = "SELECT user_id AS id, email, login, name, birthday FROM user"
            + " WHERE user_id = ?";
        return jdbcTemplate.query(
            sql, this::mapRowToUser, id
        ).stream().findAny();
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO user (email, login, name, birthday) VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            injectId(user, keyHolder.getKey().longValue());
        } else {
            String sql = "UPDATE user SET email = ?, login = ?, name = ?, birthday = ?"
                + " WHERE user_id = ?";
            jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        }
    }

    @Override
    public void save(Friendship friendship) {
        String sql = "MERGE INTO friendship (inviter_id, acceptor_id, is_confirmed)"
            + " KEY (inviter_id, acceptor_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendship.getInviterId(), friendship.getAcceptorId(),
            friendship.isConfirmed());
    }

    @Override
    public void delete(Friendship friendship) {
        String sql = "DELETE FROM friendship WHERE inviter_id = ? AND acceptor_id = ?";
        jdbcTemplate.update(sql, friendship.getInviterId(), friendship.getAcceptorId());
    }

    @Override
    public Optional<Friendship> getFriendshipMetadataByUserIds(long userId, long otherId) {
        String sql = "SELECT inviter_id, acceptor_id, is_confirmed FROM friendship"
            + " WHERE (inviter_id = ? AND acceptor_id = ?)"
            + " OR (acceptor_id = ? AND inviter_id = ?)";
        return jdbcTemplate.query(sql, this::mapRowToFriendship,
            userId, otherId,
            userId, otherId
        ).stream().findAny();
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT user_id AS id, email, login, name, birthday FROM user";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public Collection<User> getFriendsOfUser(long userId) {
        String sql = "SELECT user_id AS id, email, login, name, birthday"
            + " FROM user AS u"
            + " WHERE u.user_id IN ("
            + "   (SELECT acceptor_id AS user_id FROM friendship WHERE inviter_id = ?)"
            + "   UNION"
            + "   (SELECT inviter_id AS user_id FROM friendship WHERE acceptor_id = ?"
            + "      AND is_confirmed IS TRUE)"
            + " )";
        return jdbcTemplate.query(sql, this::mapRowToUser,
            userId, userId);
    }

    @Override
    public Collection<User> getCommonFriendsOfUsers(long userId, long otherId) {
        String sql = "SELECT user_id AS id, email, login, name, birthday"
            + " FROM user AS u"
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
        return jdbcTemplate.query(sql, this::mapRowToUser,
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
