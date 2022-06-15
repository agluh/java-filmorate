package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model of MPA rating.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MpaRating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String name;

    MpaRating(String name) {
        this.name = name;
    }

    public int getId() {
        return ordinal() + 1;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static MpaRating forObject(@JsonProperty("id") int id) {
        return MpaRating.values()[id - 1];
    }
}
