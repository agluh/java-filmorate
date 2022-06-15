package ru.yandex.practicum.filmorate.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Event;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventDbStorageTest {

    private final EventDbStorage eventStorage;

    @Test
    public void testGetEvent() {
        Optional<Event> eventOptional = eventStorage.getEvent(1);

        assertThat(eventOptional)
            .isPresent()
            .hasValueSatisfying(event ->
                assertThat(event).hasFieldOrPropertyWithValue("eventId", 1L)
            );
    }

    @Test
    void testSaveEvent() {
        Event event = new Event(null, 1, 2,
            ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")),
            "FRIEND", "ADD");

        eventStorage.save(event);

        assertThat(event.getEventId()).isNotNull();

        Optional<Event> eventOptional = eventStorage.getEvent(event.getEventId());

        assertThat(eventOptional)
            .isPresent()
            .hasValueSatisfying(e ->
                assertThat(e).isEqualTo(event)
            );
    }

    @Test
    public void testGetEventsOfUser() {
        Collection<Event> events = eventStorage.getEventsListForUser(1);

        assertThat(events).hasSize(1);
    }
}