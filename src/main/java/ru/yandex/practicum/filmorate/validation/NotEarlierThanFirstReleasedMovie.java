package ru.yandex.practicum.filmorate.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Marks fields for validation by date.
 */
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = NotEarlierThanFirstReleasedMovieValidator.class)
@Documented
public @interface NotEarlierThanFirstReleasedMovie {

    String message() default "date must be after release date of first commercial movie";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
