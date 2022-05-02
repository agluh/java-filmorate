package ru.yandex.practicum.filmorate.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String NAME = "name";
    private static final String LOGIN = "login";
    private static final String EMAIL = "mail@example.com";
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);
    private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private FriendshipService friendshipService;

    @Test
    void givenListOfUsers_shouldReturnCode200AndCorrectData() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(createUser()));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$[0].login", is(LOGIN)))
            .andExpect(jsonPath("$[0].email", is(EMAIL)))
            .andExpect(jsonPath("$[0].name", is(NAME)))
            .andExpect(jsonPath("$[0].birthday", is(BIRTHDAY.format(BIRTHDAY_FORMATTER))));
    }

    @Test
    void whenCreateUserWithNullLogin_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"login\":null,"
            + "\"name\":\"name\","
            + "\"email\":\"test@example.com\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(post("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("login")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not be null")));
    }

    @Test
    void whenCreateUserWithLoginWithSpaces_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"login\":\"some login\","
            + "\"name\":\"name\","
            + "\"email\":\"test@example.com\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(post("/users")
                .content(userInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("login")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not contain whitespace characters")));
    }

    @Test
    void whenCreateUserWithInvalidEmail_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"login\":\"login\","
            + "\"name\":\"name\","
            + "\"email\":\"bad address\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(post("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("email")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be a well-formed email address")));
    }

    @Test
    void whenCreateUserWithBirthdayInFuture_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"login\":\"login\","
            + "\"name\":\"name\","
            + "\"email\":\"email@example.com\","
            + "\"birthday\":\"" + LocalDate.now().plusDays(1).format(BIRTHDAY_FORMATTER) + "\""
            + "}";

        mockMvc.perform(post("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("birthday")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be a date in the past or in the present")));
    }

    @Test
    void whenUpdateUserWithNullLogin_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"id\":1,"
            + "\"login\":null,"
            + "\"name\":\"name\","
            + "\"email\":\"test@example.com\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(put("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("login")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not be null")));
    }

    @Test
    void whenUpdateUserWithLoginWithSpaces_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"id\":1,"
            + "\"login\":\"some login\","
            + "\"name\":\"name\","
            + "\"email\":\"test@example.com\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(put("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("login")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not contain whitespace characters")));
    }

    @Test
    void whenUpdateUserWithInvalidEmail_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"id\":1,"
            + "\"login\":\"login\","
            + "\"name\":\"name\","
            + "\"email\":\"bad address\","
            + "\"birthday\":\"1988-08-20\""
            + "}";

        mockMvc.perform(put("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("email")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be a well-formed email address")));
    }

    @Test
    void whenUpdateUserWithBirthdayInFuture_shouldReturnCode400() throws Exception {
        String userInJson = "{"
            + "\"id\":1,"
            + "\"login\":\"login\","
            + "\"name\":\"name\","
            + "\"email\":\"email@example.com\","
            + "\"birthday\":\"" + LocalDate.now().plusDays(1).format(BIRTHDAY_FORMATTER)
            + "\""
            + "}";

        mockMvc.perform(put("/users")
            .content(userInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("birthday")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be a date in the past or in the present")));
    }

    private User createUser() {
        return new User(1L, EMAIL, LOGIN, NAME, BIRTHDAY);
    }
}