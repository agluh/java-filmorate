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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPAA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final long DURATION = 100;
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final DateTimeFormatter RELEASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    @MockBean
    private LikeService likeService;

    @Test
    void givenListOfFilms_shouldReturnCode200AndCorrectData() throws Exception {
        when(service.getAllFilms()).thenReturn(List.of(createFilm()));

        mockMvc.perform(get("/films"))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$[0].description", is(DESCRIPTION)))
            .andExpect(jsonPath("$[0].duration", is((int)DURATION)))
            .andExpect(jsonPath("$[0].name", is(NAME)))
            .andExpect(jsonPath("$[0].releaseDate", is(RELEASE_DATE.format(RELEASE_DATE_FORMATTER))));
    }

    @Test
    void whenCreateFilmWithEmptyName_shouldReturnCode400() throws Exception {
        String filmInJson = "{"
            + "\"name\":\"\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1988-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(post("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("name")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not be empty")));
    }

    @Test
    void whenCreateFilmWithDescriptionMoreThan200Characters_shouldReturnCode400() throws Exception {
        String filmInJson = "{"
            + "\"name\":\"name\","
            + "\"description\":\"" + "a".repeat(201) + "\","
            + "\"releaseDate\":\"1988-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(post("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("description")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("size must be between 0 and 200")));
    }

    @Test
    void whenCreateFilmWithReleaseDateAfterFirstMovieReleaseDate_shouldReturnCode400()
            throws Exception {
        String filmInJson = "{"
            + "\"name\":\"name\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1800-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(post("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("releaseDate")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("date must be after release date of first commercial movie")));
    }

    @Test
    void whenCreateFilmWithNegativeOrZeroDuration_shouldReturnCode400()
        throws Exception {
        String filmInJson = "{"
            + "\"name\":\"name\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1800-08-20\","
            + "\"duration\":-1"
            + "}";

        mockMvc.perform(post("/films")
                .content(filmInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("duration")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be greater than 0")));
    }




    @Test
    void whenUpdateFilmWithEmptyName_shouldReturnCode400() throws Exception {
        String filmInJson = "{"
            + "\"id\":1,"
            + "\"name\":\"\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1988-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(put("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("name")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must not be empty")));
    }

    @Test
    void whenUpdateFilmWithDescriptionMoreThan200Characters_shouldReturnCode400() throws Exception {
        String filmInJson = "{"
            + "\"id\":1,"
            + "\"name\":\"name\","
            + "\"description\":\"" + "a".repeat(201) + "\","
            + "\"releaseDate\":\"1988-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(put("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("description")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("size must be between 0 and 200")));
    }

    @Test
    void whenUpdateFilmWithReleaseDateAfterFirstMovieReleaseDate_shouldReturnCode400()
        throws Exception {
        String filmInJson = "{"
            + "\"id\":1,"
            + "\"name\":\"name\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1800-08-20\","
            + "\"duration\":100"
            + "}";

        mockMvc.perform(put("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("releaseDate")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("date must be after release date of first commercial movie")));
    }

    @Test
    void whenUpdateFilmWithNegativeOrZeroDuration_shouldReturnCode400()
        throws Exception {
        String filmInJson = "{"
            + "\"id\":1,"
            + "\"name\":\"name\","
            + "\"description\":\"description\","
            + "\"releaseDate\":\"1800-08-20\","
            + "\"duration\":-1"
            + "}";

        mockMvc.perform(put("/films")
            .content(filmInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", is(notNullValue())))
            .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
            .andExpect(jsonPath("$.message", is("Validation error")))
            .andExpect(jsonPath("$.subErrors").isArray())
            .andExpect(jsonPath("$.subErrors.[0].field",
                is("duration")))
            .andExpect(jsonPath("$.subErrors.[0].message",
                is("must be greater than 0")));
    }


    private Film createFilm() {
        return new Film(1L, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPAA.G);
    }
}