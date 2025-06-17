package com.ketan.restaurant.services.impl;

import com.ketan.restaurant.domain.GeoLocation;
import com.ketan.restaurant.domain.RestaurantCreateUpdateRequest;
import com.ketan.restaurant.domain.entities.Address;
import com.ketan.restaurant.domain.entities.Photo;
import com.ketan.restaurant.domain.entities.Restaurant;
import com.ketan.restaurant.exceptions.RestaurantNotFoundException;
import com.ketan.restaurant.repositories.RestaurantRepository;
import com.ketan.restaurant.services.GeoLocationService;
import com.ketan.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {

        Address address=request.getAddress();
        GeoLocation geoLocation=geoLocationService.geoLocate(address);
        GeoPoint geoPoint= new GeoPoint(geoLocation.getLatitude(),geoLocation.getLongitude());

        List<String> photoIds=request.getPhotoIds();
        List<Photo> photos= photoIds.stream().map(photoUrl-> Photo.builder()
                .url(photoUrl).uploadDate(LocalDateTime.now())
                .build())
                .toList();

       Restaurant restaurant=Restaurant.builder()
               .name(request.getName())
               .cuisineType(request.getCuisineType())
               .contactInformation(request.getContactInformation())
               .address(address)
               .geoLocation(geoPoint)
               .operatingHours(request.getOperatingHours())
               .averageRating(0f)
               .photos(photos)
               .build();

       return restaurantRepository.save(restaurant);

    }

    @Override
    public Page<Restaurant> searchRestaurants(String query, Float minRating, Float latitude, Float longitude, Float radius, Pageable pageable) {
        if(null != minRating && (null == query || query.isEmpty())){
            return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating,pageable);
        }

        Float searchMinRating = null == minRating ? 0f : minRating;

        if(null != query && !query.trim().isEmpty()){
            return restaurantRepository.findByQueryAndMinRating(query,searchMinRating,pageable);
        }

        if(null != latitude && null != longitude && null!=radius){
            return restaurantRepository.findByLocationNear(latitude,longitude,radius,pageable);
        }

        return restaurantRepository.findAll(pageable);

    }

    @Override
    public Optional<Restaurant> getRestaurant(String id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest request) {
        Restaurant restaurant=getRestaurant(id)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant with ID does not exist: "+id));

        GeoLocation newGeoLocation=geoLocationService.geoLocate(request.getAddress());

        GeoPoint newGeoPoint=new GeoPoint(newGeoLocation.getLatitude(), newGeoLocation.getLongitude());

        List<String> photoIds=request.getPhotoIds();
        List<Photo> photos= photoIds.stream().map(photoUrl-> Photo.builder()
                        .url(photoUrl).uploadDate(LocalDateTime.now())
                        .build())
                        .toList();

        restaurant.setName(request.getName());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setAddress(request.getAddress());
        restaurant.setContactInformation(request.getContactInformation());
        restaurant.setGeoLocation(newGeoPoint);
        restaurant.setOperatingHours(request.getOperatingHours());
        restaurant.setPhotos(photos);

        return restaurantRepository.save(restaurant);

    }

    @Override
    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }
}
