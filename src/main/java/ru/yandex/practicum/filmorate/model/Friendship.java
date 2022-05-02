package ru.yandex.practicum.filmorate.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents bidirectional link between friends.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Friendship {

    /**
     * User that originates invitation of friendship.
     */
    private final long inviterId;

    /**
     * User that accepts invitation of friendship.
     */
    private final long acceptorId;

    @EqualsAndHashCode.Exclude
    private final ZonedDateTime createdAt;
}
