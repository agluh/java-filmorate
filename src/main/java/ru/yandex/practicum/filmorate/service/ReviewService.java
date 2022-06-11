package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.events.UserLeavedReview;
import ru.yandex.practicum.filmorate.events.UserRemovedReview;
import ru.yandex.practicum.filmorate.events.UserUpdatedReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.service.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.service.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.service.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.*;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Collection;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewReadModel reviewReadModel;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage,
                         FilmStorage filmStorage, ReviewReadModel reviewReadModel,
                         ReviewLikeStorage reviewLikeStorage, ApplicationEventPublisher eventPublisher) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.reviewReadModel = reviewReadModel;
        this.reviewLikeStorage = reviewLikeStorage;
        this.eventPublisher = eventPublisher;
    }

    public Review getReview(long reviewId) {
        return reviewStorage.getReview(reviewId).orElseThrow(() ->
                new ReviewNotFoundException(reviewId));
    }

    public Collection<Review> getAllReviewsByFilmId(long reviewId, int count) {
        ensureFilmExists(reviewId);
        return reviewReadModel.getReviewsByFilmId(reviewId,count);
    }

    @Transactional
    public Review createReview(Review review) {
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());

        reviewStorage.save(review);

        eventPublisher.publishEvent(new UserLeavedReview(ZonedDateTime.now(), review.getUserId(), review.getId()));
        return review;
    }

    @Transactional
    public Review updateReview(Review review) {
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());

        long id = review.getId();
        Review existedReview = reviewStorage.getReview(id).orElseThrow(() ->
                new ReviewNotFoundException(id));
        existedReview.setUserId(review.getUserId());
        existedReview.setFilmId(review.getFilmId());
        existedReview.setPositive(review.isPositive());
        existedReview.setContent(review.getContent());

        reviewStorage.save(existedReview);
        eventPublisher.publishEvent(new UserUpdatedReview(ZonedDateTime.now(), review.getUserId(), review.getId()));
        return existedReview;
    }

    public void delete(long reviewId) {
        Review review = getReview(reviewId);
        reviewStorage.delete(review.getId());
        eventPublisher.publishEvent(new UserRemovedReview(ZonedDateTime.now(), review.getUserId(), review.getId()));
    }

    public void likeReview(long userId, long reviewId) {
        ensureReviewExists(reviewId);
        ensureUserExists(userId);
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, true);
        reviewLikeStorage.save(reviewLike);
    }

    public void dislikeReview(long userId, long reviewId) {
        ensureReviewExists(reviewId);
        ensureUserExists(userId);
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, false);
        reviewLikeStorage.save(reviewLike);
    }

    public void deleteMark(long userId, long reviewId) {
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, false);
        reviewLikeStorage.delete(reviewLike);
    }

    private void ensureFilmExists(long filmId) {
        filmStorage.getFilm(filmId).orElseThrow(() ->
                new FilmNotFoundException(filmId));
    }

    private void ensureUserExists(long userId) {
        userStorage.getUser(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
    }

    private void ensureReviewExists(long reviewId) {
        reviewStorage.getReview(reviewId).orElseThrow(() ->
                new ReviewNotFoundException(reviewId));
    }
}
