package com.dev.restaurant.mappers;

import com.dev.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.dev.restaurant.domain.dtos.ReviewDto;
import com.dev.restaurant.domain.entities.Review;
import com.dev.restaurant.domain.requests.ReviewCreateUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateRequestDto dto);

    ReviewDto toReviewDto(Review review);
}
