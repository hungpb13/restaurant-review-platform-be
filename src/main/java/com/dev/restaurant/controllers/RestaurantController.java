package com.dev.restaurant.controllers;

import com.dev.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.dev.restaurant.domain.dtos.RestaurantDto;
import com.dev.restaurant.domain.entities.Restaurant;
import com.dev.restaurant.domain.requests.RestaurantCreateUpdateRequest;
import com.dev.restaurant.mappers.RestaurantMapper;
import com.dev.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(
            @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto
    ) {
        RestaurantCreateUpdateRequest request = restaurantMapper
                .toRestaurantCreateUpdateRequest(requestDto);

        Restaurant restaurant = restaurantService.createRestaurant(request);

        return new ResponseEntity<>(
                restaurantMapper.toRestaurantDto(restaurant),
                HttpStatus.CREATED
        );
    }
}
