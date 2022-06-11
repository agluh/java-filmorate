package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

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
