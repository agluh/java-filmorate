package ru.yandex.practicum.filmorate.storage.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventReadModel;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.exceptions.DaoException;

/**
 * DB based implementation of events storage.
 */
@Component
public class EventDbStorage implements EventStorage, EventReadModel {

    public static final String INSERT_EVENT =
        "INSERT INTO events (user_id, entity_id, occurred_on, event_type, operation)"
            + " VALUES(?, ?, ?, ?, ?)";
    public static final String SELECT_EVENT =
        "SELECT event_id, user_id, entity_id, occurred_on, event_type, operation"
            + " FROM events WHERE event_id = ?";
    public static final String SELECT_EVENTS =
        "SELECT event_id, user_id, entity_id, occurred_on, event_type, operation"
            + " FROM events WHERE user_id IN ("
            + "     SELECT user_id"
            + "     FROM users AS u"
            + "     WHERE u.user_id IN ("
            + "         (SELECT acceptor_id AS user_id FROM friendship WHERE inviter_id = ?)"
            + "         UNION"
            + "         (SELECT inviter_id AS user_id FROM friendship WHERE acceptor_id = ?"
            + "             AND is_confirmed IS TRUE)"
            + "     )"
            + " )"
            + " ORDER BY occurred_on DESC";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Event> getEvent(long id) {
        return jdbcTemplate.query(SELECT_EVENT, this::mapRowToEvent, id).stream().findAny();
    }

    @Override
    public void save(Event event) {
        if (event.getEventId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_EVENT,
                    new String[]{"event_id"});
                ps.setLong(1, event.getUserId());
                ps.setLong(2, event.getEntityId());
                ps.setObject(3, event.getOccurredOn());
                ps.setString(4, event.getEventType());
                ps.setString(5, event.getOperation());
                return ps;
            }, keyHolder);

            injectId(event, keyHolder.getKey().longValue());
        }
    }

    @Override
    public Collection<Event> getEventsListForUser(long userId) {
        return jdbcTemplate.query(SELECT_EVENTS, this::mapRowToEvent, userId, userId);
    }

    private void injectId(Event event, long id) {
        try {
            Field idField = Event.class.getDeclaredField("eventId");
            idField.setAccessible(true);
            idField.set(event, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
            rs.getLong("event_id"),
            rs.getLong("user_id"),
            rs.getLong("entity_id"),
            rs.getObject("occurred_on", ZonedDateTime.class),
            rs.getString("event_type"),
            rs.getString("operation")
        );
    }
}
