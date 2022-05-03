package ru.yandex.practicum.filmorate.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents like for a film.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Like {
    private final long userid;
    private final long filmId;

    @EqualsAndHashCode.Exclude
    private final ZonedDateTime createdAt;
}
