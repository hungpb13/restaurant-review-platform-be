package com.dev.restaurant.controllers;

import com.dev.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.dev.restaurant.domain.dtos.ReviewDto;
import com.dev.restaurant.domain.entities.Review;
import com.dev.restaurant.domain.entities.User;
import com.dev.restaurant.domain.requests.ReviewCreateUpdateRequest;
import com.dev.restaurant.mappers.ReviewMapper;
import com.dev.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable("restaurantId") String restaurantId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewCreateUpdateRequest request = reviewMapper.toReviewCreateUpdateRequest(requestDto);

        User user = jwtToUser(jwt);

        Review createdReview = reviewService.createReview(user, restaurantId, request);

        return new ResponseEntity<>(
                reviewMapper.toReviewDto(createdReview),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public Page<ReviewDto> getReviews(
            @PathVariable("restaurantId") String restaurantId,
            @PageableDefault(
                    size = 20,
                    page = 0,
                    sort = "datePosted",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return reviewService
                .getReviews(restaurantId, pageable)
                .map(reviewMapper::toReviewDto);
    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId
    ) {
        return reviewService.getReview(restaurantId, reviewId)
                .map(reviewMapper::toReviewDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewCreateUpdateRequest request = reviewMapper.toReviewCreateUpdateRequest(requestDto);

        User user = jwtToUser(jwt);

        Review updatedReview = reviewService.updateReview(user, restaurantId, reviewId, request);

        return ResponseEntity.ok(reviewMapper.toReviewDto(updatedReview));
    }

    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId
    ) {
        reviewService.deleteReview(restaurantId, reviewId);
        return ResponseEntity.noContent().build();
    }

    private User jwtToUser(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
