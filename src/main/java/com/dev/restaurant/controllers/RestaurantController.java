package com.dev.restaurant.controllers;

import com.dev.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.dev.restaurant.domain.dtos.RestaurantDto;
import com.dev.restaurant.domain.dtos.RestaurantSummaryDto;
import com.dev.restaurant.domain.entities.Restaurant;
import com.dev.restaurant.domain.requests.RestaurantCreateUpdateRequest;
import com.dev.restaurant.mappers.RestaurantMapper;
import com.dev.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Page<RestaurantSummaryDto> searchRestaurants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float latitude,
            @RequestParam(required = false) Float longitude,
            @RequestParam(required = false) Float radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<Restaurant> searchResults = restaurantService.searchRestaurants(
                q, minRating, latitude, longitude, radius, PageRequest.of(page - 1, size)
        );

        return searchResults.map(restaurantMapper::toRestaurantSummaryDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("id") String id) {
        return restaurantService.getRestaurant(id)
                .map(restaurant ->
                        ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant))
                ).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable("id") String id,
            @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto
    ) {
        RestaurantCreateUpdateRequest request = restaurantMapper
                .toRestaurantCreateUpdateRequest(requestDto);

        Restaurant restaurant = restaurantService.updateRestaurant(id, request);

        return ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
