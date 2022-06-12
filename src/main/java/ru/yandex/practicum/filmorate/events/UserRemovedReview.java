package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user removed a film review.
 */
@Getter
public final class UserRemovedReview extends UserDomainEvent {

    private final long reviewId;

    public UserRemovedReview(ZonedDateTime occurredOn, long userId, long reviewId) {
        super(occurredOn, userId);
        this.reviewId = reviewId;
    }
}

