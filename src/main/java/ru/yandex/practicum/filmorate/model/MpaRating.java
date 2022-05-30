package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MpaRating {
    G("G - у фильма нет возрастных ограничений"),
    PG("PG - детям рекомендуется смотреть фильм с родителями"),
    PG_13("PG-13 - детям до 13 лет просмотр не желателен"),
    R("R - лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17("NC-17 - лицам до 18 лет просмотр запрещён");

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
