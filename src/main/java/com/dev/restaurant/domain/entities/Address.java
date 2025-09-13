package com.dev.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Field(type = FieldType.Keyword)
    private String streetNumber;

    @Field(type = FieldType.Text)
    private String streetName;

    @Field(type = FieldType.Keyword)
    private String unit;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String state;

    @Field(type = FieldType.Keyword)
    private String postalCode;

    @Field(type = FieldType.Keyword)
    private String country;
}
