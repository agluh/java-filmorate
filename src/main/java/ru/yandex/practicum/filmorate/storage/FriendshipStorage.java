package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Friendship;

/**
 * Repository interface fir friendship.
 */
public interface FriendshipStorage {

    void save(Friendship friendship);

    void delete(Friendship friendship);

    Optional<Friendship> getFriendshipMetadataByUserIds(long userId, long otherId);

    Collection<Friendship> getFriendshipMetadataOfUser(long userId);
}
