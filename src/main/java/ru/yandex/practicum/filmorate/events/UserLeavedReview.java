package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user leaved a film review.
 */
@Getter
public final class UserLeavedReview extends UserDomainEvent {

    private final long reviewId;

    public UserLeavedReview(ZonedDateTime occurredOn, long userId, long reviewId) {
        super(occurredOn, userId);
        this.reviewId = reviewId;
    }
}
