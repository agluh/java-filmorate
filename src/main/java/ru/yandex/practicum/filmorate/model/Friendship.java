package ru.yandex.practicum.filmorate.model;

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

    private boolean isConfirmed;

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public boolean isInitiatedBy(long userId) {
        return inviterId == userId;
    }
}
