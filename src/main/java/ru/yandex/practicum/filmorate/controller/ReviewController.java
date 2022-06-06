package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validation.ValidationMarker;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/review")
@Slf4j
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Adds a new review.
     */
    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public Review addReview(@Valid @RequestBody Review review) {
        Review newReview = reviewService.createReview(review);
        log.info("Added a new review {}", newReview);
        return newReview;
    }

    /**
     * Update an existing review.
     */
    @PutMapping
    @Validated(ValidationMarker.OnUpdate.class)
    public Review updateReview(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(review);
        log.info("Updated review {}", updatedReview);
        return updatedReview;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") long reviewId) {
        return reviewService.getReview(reviewId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable("id") long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping
    public Collection<Review> getAllReviewsByFilmId(
            @RequestParam(value = "film") long filmId,
            @RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    /**
     * Like review by user.
     */
    @PutMapping("/{id}/like/{userId}/")
    public ResponseEntity<?> likeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("User {} liked review {}", userId, reviewId);
        reviewService.likeReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Dislike review by user.
     */
    @PutMapping("/{id}/dislike/{userId}/")
    public ResponseEntity<?> dislikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("User {} disliked review {}", userId, reviewId);
        reviewService.dislikeReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/{id}/like/{userId}/")
    public ResponseEntity<?> deleteLikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("User {} deleted like on review {}", userId, reviewId);
        reviewService.deleteMark(userId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/{id}/dislike/{userId}/")
    public ResponseEntity<?> deleteDislikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("User {} deleted dislike on review {}", userId, reviewId);
        reviewService.deleteMark(userId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
