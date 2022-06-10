package ru.yandex.practicum.filmorate.events;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * Represents an event when user adds other to friends.
 */
@Getter
public final class UserAddedFriend extends UserDomainEvent {

    private final long friendId;

    public UserAddedFriend(ZonedDateTime occurredOn, long userId, long friendId) {
        super(occurredOn, userId);
        this.friendId = friendId;
    }
}
