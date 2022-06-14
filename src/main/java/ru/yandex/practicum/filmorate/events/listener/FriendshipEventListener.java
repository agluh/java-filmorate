package ru.yandex.practicum.filmorate.events.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.UserAddedFriend;
import ru.yandex.practicum.filmorate.events.UserRemovedFriend;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

/**
 * Listener of events related to friendship.
 */
@Component
@AllArgsConstructor
public class FriendshipEventListener {

    private static final String EVENT_SUBJECT_FRIEND = "FRIEND";
    private static final String EVENT_OPERATION_ADD = "ADD";
    private static final String EVENT_OPERATION_REMOVE = "REMOVE";

    private final EventStorage eventStorage;

    @EventListener(UserAddedFriend.class)
    public void onUserAddedFriend(UserAddedFriend event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getFriendId(),
            event.getOccurredOn(),
            EVENT_SUBJECT_FRIEND,
            EVENT_OPERATION_ADD
        ));
    }

    @EventListener(UserRemovedFriend.class)
    public void onUserRemovedFriend(UserRemovedFriend event) {
        eventStorage.save(new Event(
            null,
            event.getUserId(),
            event.getFriendId(),
            event.getOccurredOn(),
            EVENT_SUBJECT_FRIEND,
            EVENT_OPERATION_REMOVE
        ));
    }
}
