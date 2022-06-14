package ru.yandex.practicum.filmorate.events.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.UserLikedFilm;
import ru.yandex.practicum.filmorate.events.UserRevokedLikeOfFilm;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

/**
 * Listener of events related to film likes.
 */
@Component
@AllArgsConstructor
public class LikeEventListener {

    private static final String LIKE = "LIKE";
    private static final String ADD = "ADD";
    private static final String REMOVE = "REMOVE";

    private final EventStorage eventStorage;

    @EventListener(UserLikedFilm.class)
    public void onUserLikedFilm(UserLikedFilm event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getFilmId(),
            event.getOccurredOn(),
            LIKE,
            ADD
        ));
    }

    @EventListener(UserRevokedLikeOfFilm.class)
    public void onUserRevokedLikeOfFilm(UserRevokedLikeOfFilm event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getFilmId(),
            event.getOccurredOn(),
            LIKE,
            REMOVE
        ));
    }
}
