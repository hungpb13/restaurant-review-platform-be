package com.dev.restaurant.services;

import com.dev.restaurant.domain.entities.Review;
import com.dev.restaurant.domain.entities.User;
import com.dev.restaurant.domain.requests.ReviewCreateUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);

    Page<Review> getReviews(String restaurantId, Pageable pageable);
}
