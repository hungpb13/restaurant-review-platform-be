package com.dev.restaurant.services;

import com.dev.restaurant.domain.entities.Review;
import com.dev.restaurant.domain.entities.User;
import com.dev.restaurant.domain.requests.ReviewCreateUpdateRequest;

public interface ReviewService {
    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);
}
