package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user unfriend other user.
 */
@Getter
public final class UserRemovedFriend extends UserDomainEvent {

    private final long friendId;

    public UserRemovedFriend(ZonedDateTime occurredOn, long userId, long friendId) {
        super(occurredOn, userId);
        this.friendId = friendId;
    }
}
