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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
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

    @Override
    public Page<Review> getReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        List<Review> reviews = restaurant.getReviews();

        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean isAscending = order.getDirection().isAscending();

            Comparator<Review> comparator = switch (property) {
                case "datePosted" -> Comparator.comparing(Review::getDatePosted);
                case "rating" -> Comparator.comparing(Review::getRating);
                default -> Comparator.comparing(Review::getDatePosted);
            };

            reviews.sort(isAscending ? comparator : comparator.reversed());
        } else {
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
        }

        int start = (int) pageable.getOffset();

        if (start >= reviews.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, reviews.size());
        }

        int end = Math.min((start + pageable.getPageSize()), reviews.size());

        return new PageImpl<>(reviews.subList(start, end), pageable, reviews.size());
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
