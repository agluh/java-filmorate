package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

/**
 * Controller for users.
 */
@Validated
@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    /**
     * Adds a new user.
     */
    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public User addUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        log.info("Add a new user {}", newUser);
        return newUser;
    }

    /**
     * Update an existing user.
     */
    @PutMapping
    @Validated(ValidationMarker.OnUpdate.class)
    public User updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        log.info("Update user {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") long userId) {
        return userService.getUser(userId);
    }

    /**
     * Makes user friends of each other.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable("id") long userId,
        @PathVariable long friendId) {
        log.info("Make user {} friend of user {}", friendId, userId);
        boolean areNewFriends = friendshipService.makeFriends(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Unfriend two users.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable("id") long userId,
        @PathVariable long friendId) {
        log.info("Unfriend users {} and {}", userId, friendId);
        boolean wasFriends = friendshipService.unfriendUsers(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsOfUser(@PathVariable("id") long userId) {
        return friendshipService.getFriendsOfUser(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
        @PathVariable("id") long userId,
        @PathVariable long otherId) {
        return friendshipService.getCommonFriends(userId, otherId);
    }
}
