package ru.yandex.practicum.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

@SpringBootTest
class FilmorateApplicationTests {

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(filmController).isNotNull();
    }
}
