package com.dev.restaurant.mappers;

import com.dev.restaurant.domain.dtos.GeoPointDto;
import com.dev.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.dev.restaurant.domain.dtos.RestaurantDto;
import com.dev.restaurant.domain.entities.Restaurant;
import com.dev.restaurant.domain.requests.RestaurantCreateUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {

    RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(RestaurantCreateUpdateRequestDto dto);

    RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoPointDto toGeoPointDto(GeoPoint geoPoint);
}
