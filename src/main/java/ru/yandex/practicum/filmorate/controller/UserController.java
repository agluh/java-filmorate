package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

/**
 * Controller for users.
 */
@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
        try {
            User updatedUser = userService.updateUser(user);
            log.info("Update user {}", updatedUser);
            return updatedUser;
        } catch (UserNotFoundException e) {
            throw new EntityNotFoundException(e);
        }
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
