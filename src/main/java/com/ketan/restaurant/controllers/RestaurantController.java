package com.ketan.restaurant.controllers;

import com.ketan.restaurant.domain.RestaurantCreateUpdateRequest;
import com.ketan.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.ketan.restaurant.domain.dtos.RestaurantDto;
import com.ketan.restaurant.domain.dtos.RestaurantSummaryDto;
import com.ketan.restaurant.domain.entities.Restaurant;
import com.ketan.restaurant.mappers.RestaurantMapper;
import com.ketan.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    private  final RestaurantMapper restaurantMapper;


    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDto request){
        RestaurantCreateUpdateRequest restaurantCreateUpdateRequest= restaurantMapper.toRestaurantCreateUpdateRequest(request);

        Restaurant restaurant=restaurantService.createRestaurant(restaurantCreateUpdateRequest);

        RestaurantDto createdRestaurant = restaurantMapper.toRestaurantDto(restaurant);

        System.out.println("create restaurant api call");

        return ResponseEntity.ok(createdRestaurant);

    }

    @GetMapping
    public Page<RestaurantSummaryDto> searchRestaurants(@RequestParam(required = false) String q,
                                                        @RequestParam(required = false) Float minRating,
                                                        @RequestParam(required = false) Float latitude,
                                                        @RequestParam(required = false) Float longitude,
                                                        @RequestParam(required = false) Float radius,
                                                        @RequestParam(defaultValue = "1")int page,
                                                        @RequestParam(defaultValue = "28")int size
    ){
        Page<Restaurant> searchResults=restaurantService.searchRestaurants(
                q,minRating,latitude,longitude,radius, PageRequest.of(page-1,size)
        );

        searchResults.stream().toList().stream().map(restaurantMapper::toSummaryDto).forEach(rest->rest.toString());
        return searchResults.map(restaurantMapper::toSummaryDto);
    }

    @GetMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurant_id") String restaurantId){
        return restaurantService.getRestaurant(restaurantId)
                .map(restaurant -> ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable("restaurant_id") String restaurantId,
                                                          @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto ){
       RestaurantCreateUpdateRequest request= restaurantMapper
               .toRestaurantCreateUpdateRequest(requestDto);


        System.out.println(requestDto.toString());
        System.out.println(request.toString());
       Restaurant updatedRestaurant=restaurantService.updateRestaurant(restaurantId,request);
        System.out.println(updatedRestaurant.toString());
       return ResponseEntity.ok(restaurantMapper.toRestaurantDto(updatedRestaurant));
    }

    @DeleteMapping(path = "/{restaurant_id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("restaurant_id") String id){
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

}
