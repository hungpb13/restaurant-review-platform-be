package com.dev.restaurant.services.impl;

import com.dev.restaurant.domain.entities.Photo;
import com.dev.restaurant.domain.entities.Restaurant;
import com.dev.restaurant.domain.entities.Review;
import com.dev.restaurant.domain.entities.User;
import com.dev.restaurant.domain.requests.ReviewCreateUpdateRequest;
import com.dev.restaurant.exceptions.RestaurantNotFoundException;
import com.dev.restaurant.exceptions.ReviewNotAllowedException;
import com.dev.restaurant.repositories.RestaurantRepository;
import com.dev.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExistingReview = restaurant.getReviews()
                .stream()
                .anyMatch(review -> review.getWrittenBy().getId().equals(author.getId()));

        if (hasExistingReview) {
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Photo> photos = request.getPhotoIds().stream()
                .map(url -> Photo.builder()
                        .url(url)
                        .uploadDate(now)
                        .build()
                ).toList();

        String reviewId = UUID.randomUUID().toString();

        Review reviewToCreate = Review.builder()
                .id(reviewId)
                .content(request.getContent())
                .rating(request.getRating())
                .photos(photos)
                .datePosted(now)
                .lastEdited(now)
                .writtenBy(author)
                .build();

        restaurant.getReviews().add(reviewToCreate);

        updateRestaurantAverageReview(restaurant);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return savedRestaurant.getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error retrieving created review"));
    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
    }

    private void updateRestaurantAverageReview(Restaurant restaurant) {
        List<Review> reviews = restaurant.getReviews();

        if (reviews.isEmpty()) {
            restaurant.setAverageRating(0.0f);
        } else {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            restaurant.setAverageRating((float) averageRating);
        }
    }
}
