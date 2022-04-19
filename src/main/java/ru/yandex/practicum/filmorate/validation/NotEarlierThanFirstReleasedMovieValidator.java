package ru.yandex.practicum.filmorate.validation;

import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for NotEarlierThanFirstReleasedMovie constraint.
 */
public class NotEarlierThanFirstReleasedMovieValidator
    implements ConstraintValidator<NotEarlierThanFirstReleasedMovie, LocalDate> {

    public static final LocalDate FIRST_COMMERCIAL_MOVIE_RELEASE_DATE =
        LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(FIRST_COMMERCIAL_MOVIE_RELEASE_DATE);
        }

        return false;
    }
}
