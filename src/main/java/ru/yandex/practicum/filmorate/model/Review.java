package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {

    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class)
    @EqualsAndHashCode.Include
    private final Long id;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private long userId;
    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private long filmId;
    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private boolean isPositive;
    @NotEmpty(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private String content;

    private Integer useful;
}
