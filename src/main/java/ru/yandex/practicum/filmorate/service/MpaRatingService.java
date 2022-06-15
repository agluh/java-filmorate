package ru.yandex.practicum.filmorate.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.exception.MpaRatingNotFoundException;

/**
 * Provides service layer for MPA management.
 */
@Service
public class MpaRatingService {

    public MpaRating getMpaRating(int mpaId) {
        if (mpaId < 0 || mpaId > MpaRating.values().length - 1) {
            throw new MpaRatingNotFoundException(mpaId);
        }

        return MpaRating.forObject(mpaId);
    }

    public Collection<MpaRating> getAllMpaRatings() {
        return Arrays.stream(MpaRating.values()).collect(Collectors.toList());
    }
}
