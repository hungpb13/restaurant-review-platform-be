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

    private User jwtToUser(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
