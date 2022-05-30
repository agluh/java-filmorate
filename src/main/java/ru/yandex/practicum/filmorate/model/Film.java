package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validation.NotEarlierThanFirstReleasedMovie;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

/**
 * Film model.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {

    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class)
    @EqualsAndHashCode.Include
    private final Long id;

    @NotEmpty(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private String name;

    @Size(max = 200, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @NotEmpty(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private String description;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @NotEarlierThanFirstReleasedMovie(groups = {
        ValidationMarker.OnCreate.class,
        ValidationMarker.OnUpdate.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    /** Film duration in seconds. */
    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @Positive
    private long duration;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private MpaRating mpa;
}
