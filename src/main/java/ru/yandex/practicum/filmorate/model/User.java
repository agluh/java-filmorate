package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

/**
 * User model.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class)
    @EqualsAndHashCode.Include
    private final Long id;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @Email(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private String email;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @Pattern(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class},
        regexp = "\\S+", message = "must not contain whitespace characters")
    private String login;

    private String name;

    @NotNull(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @PastOrPresent(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
