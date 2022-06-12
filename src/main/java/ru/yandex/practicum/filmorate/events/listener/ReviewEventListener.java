package ru.yandex.practicum.filmorate.events.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.UserLeavedReview;
import ru.yandex.practicum.filmorate.events.UserRemovedReview;
import ru.yandex.practicum.filmorate.events.UserUpdatedReview;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

/**
 * Listener of events related to film reviews.
 */
@Component
@AllArgsConstructor
public class ReviewEventListener {

    private final EventStorage eventStorage;

    @EventListener(UserLeavedReview.class)
    public void onUserLeavedReview(UserLeavedReview event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getReviewId(),
            event.getOccurredOn(),
            "REVIEW",
            "ADD"
        ));
    }

    @EventListener(UserUpdatedReview.class)
    public void onUserUpdatedReview(UserUpdatedReview event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getReviewId(),
            event.getOccurredOn(),
            "REVIEW",
            "UPDATE"
        ));
    }

    @EventListener(UserRemovedReview.class)
    public void onUserRemovedReview(UserRemovedReview event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getReviewId(),
            event.getOccurredOn(),
            "REVIEW",
            "REMOVE"
        ));
    }
}
