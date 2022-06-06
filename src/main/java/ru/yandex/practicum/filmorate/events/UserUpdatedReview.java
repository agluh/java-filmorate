package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user updated a film review.
 */
@Getter
public final class UserUpdatedReview extends UserDomainEvent {

    private final long reviewId;

    public UserUpdatedReview(ZonedDateTime occurredOn, long userId, long reviewId) {
        super(occurredOn, userId);
        this.reviewId = reviewId;
    }
}

