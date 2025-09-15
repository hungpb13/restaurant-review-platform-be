package com.dev.restaurant.services;

import com.dev.restaurant.domain.GeoLocation;
import com.dev.restaurant.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation geoLocation(Address address);
}
