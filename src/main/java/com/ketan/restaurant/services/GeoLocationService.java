package com.ketan.restaurant.services;

import com.ketan.restaurant.domain.GeoLocation;
import com.ketan.restaurant.domain.entities.Address;

public interface GeoLocationService {

    GeoLocation geoLocate(Address address);

}
