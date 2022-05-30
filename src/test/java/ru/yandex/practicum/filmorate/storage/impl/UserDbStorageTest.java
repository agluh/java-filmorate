package ru.yandex.practicum.filmorate.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testGetUser() {
        Optional<User> userOptional = userStorage.getUser(1);

        assertThat(userOptional)
            .isPresent()
            .hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
            );
    }

    @Test
    void testGetAll() {
        Collection<User> users = userStorage.getAll();

        assertThat(users).hasSize(3);
    }

    @Test
    void testGetFriendsOfUser() {
        Collection<User> friends = userStorage.getFriendsOfUser(1);

        assertThat(friends)
            .hasSize(2)
            .flatMap(User::getId)
                .isSubsetOf(2L, 3L);
    }

    @Test
    void testGetCommonFriendsOfUsers() {
        Collection<User> friends = userStorage.getCommonFriendsOfUsers(1, 2);

        assertThat(friends)
            .hasSize(1)
            .flatMap(User::getId)
                .isSubsetOf(3L);
    }

    @Test
    void testSaveUser() {
        User user = new User(null, "bob@example.com",
            "bob", "Bob Martin", LocalDate.now());

        userStorage.save(user);

        assertThat(user.getId()).isNotNull();

        Optional<User> userOptional = userStorage.getUser(user.getId());

        assertThat(userOptional)
            .isPresent()
            .hasValueSatisfying(u ->
                assertThat(u).isEqualTo(user)
            );
    }

    @Test
    void testGetFriendshipMetadataByUserIds() {
        Optional<Friendship> friendshipOptional
            = userStorage.getFriendshipMetadataByUserIds(1, 2);

        assertThat(friendshipOptional)
            .isPresent()
            .hasValueSatisfying(f -> {
                assertThat(f.getInviterId()).isEqualTo(1);
                assertThat(f.getAcceptorId()).isEqualTo(2);
                assertThat(f.isConfirmed()).isFalse();
            });
    }

    @Test
    void testDeleteFriendship() {
        Friendship friendship = userStorage.getFriendshipMetadataByUserIds(1, 2)
            .orElseThrow();

        userStorage.delete(friendship);

        Optional<Friendship> friendshipOptional
            = userStorage.getFriendshipMetadataByUserIds(1, 2);

        assertThat(friendshipOptional).isEmpty();
    }

    @Test
    void testSaveFriendship() {
        Friendship friendship = userStorage.getFriendshipMetadataByUserIds(1, 2)
            .orElseThrow();

        assertThat(friendship.isConfirmed()).isFalse();

        friendship.setConfirmed(true);
        userStorage.save(friendship);

        Friendship savedFriendship = userStorage.getFriendshipMetadataByUserIds(1, 2)
            .orElseThrow();

        assertThat(savedFriendship).isEqualTo(friendship);
    }
}