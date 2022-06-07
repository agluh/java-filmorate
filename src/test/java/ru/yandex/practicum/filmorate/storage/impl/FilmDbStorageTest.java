package ru.yandex.practicum.filmorate.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    public void testGetFilm() {
        Optional<Film> filmOptional = filmStorage.getFilm(1);

        assertThat(filmOptional)
            .isPresent()
            .hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
            );
    }

    @Test
    void testGetAll() {
        Collection<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(2);
    }

    @Test
    void testGetMostPopularFilms() {
        Collection<Film> films = filmStorage.getMostPopularFilms(1);

        assertThat(films)
            .hasSize(1)
            .flatMap(Film::getId)
                .isSubsetOf(2L);
    }

    @Test
    void testSaveFilm() {
        Film film = new Film(null, "Name", "Description",
            LocalDate.of(2022, 4, 22), 120, MpaRating.G,
            new HashSet<>());

        filmStorage.save(film);

        assertThat(film.getId()).isNotNull();

        Optional<Film> filmOptional = filmStorage.getFilm(film.getId());

        assertThat(filmOptional)
            .isPresent()
            .hasValueSatisfying(f ->
                assertThat(f).isEqualTo(film)
            );
    }

    @Test
    void testSaveLike() {
        assertThat(filmStorage.getLikeMetadataByUserAndFilm(3, 2)).isEmpty();

        Like like = new Like(3, 2, ZonedDateTime.now());
        filmStorage.save(like);

        Optional<Like> likeOptional = filmStorage.getLikeMetadataByUserAndFilm(3, 2);

        assertThat(likeOptional)
            .isPresent()
            .hasValueSatisfying(l ->
                assertThat(l).isEqualTo(like)
            );
    }

    @Test
    void testDeleteLike() {
        Like like = filmStorage.getLikeMetadataByUserAndFilm(2, 2).orElseThrow();

        filmStorage.delete(like);

        assertThat(filmStorage.getLikeMetadataByUserAndFilm(2, 2)).isEmpty();
    }
}