package com.dev.restaurant.services;

import com.dev.restaurant.domain.entities.Restaurant;
import com.dev.restaurant.domain.requests.RestaurantCreateUpdateRequest;

public interface RestaurantService {
    Restaurant createRestaurant(RestaurantCreateUpdateRequest request);
}
