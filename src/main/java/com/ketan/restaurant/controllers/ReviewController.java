package com.ketan.restaurant.controllers;

import co.elastic.clients.elasticsearch.security.get_token.UserRealm;
import com.ketan.restaurant.domain.ReviewCreateUpdateRequest;
import com.ketan.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.ketan.restaurant.domain.dtos.ReviewDto;
import com.ketan.restaurant.domain.entities.Review;
import com.ketan.restaurant.domain.entities.User;
import com.ketan.restaurant.mappers.ReviewMapper;
import com.ketan.restaurant.repositories.RestaurantRepository;
import com.ketan.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private  final ReviewMapper reviewMapper;
    private final ReviewService reviewService;
    //only for testsing
    private final RestaurantRepository restaurantRepository;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable("restaurantId") String restaurantId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto review,
            @AuthenticationPrincipal Jwt jwt
            ){


        ReviewCreateUpdateRequest reviewCreateUpdateRequest=reviewMapper.toReviewCreateUpdateRequest(review);

        User user=jwtToUser(jwt);

        Review createdReview=reviewService.createReview(user, restaurantId, reviewCreateUpdateRequest);

        return ResponseEntity.ok(reviewMapper.toDto(createdReview));
    }

    @GetMapping
    public Page<ReviewDto> listReviews(@PathVariable("restaurantId") String restaurantId,
                                       @PageableDefault(size = 20,
                                       page = 0,
                                       sort = "datePosted",
                                       direction = Sort.Direction.DESC)Pageable pageable
                                       ){
        return reviewService.listOfReviews(restaurantId,pageable)
                .map(reviewMapper::toDto);
    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable("restaurantId") String restaurantId,
                                         @PathVariable("reviewId") String reviewId){

       return  reviewService.getReview(restaurantId,reviewId)
               .map(review -> ResponseEntity.ok(reviewMapper.toDto(review)))
               .orElse(ResponseEntity.noContent().build());

    }


    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable("restaurantId") String restaurantId,
                                                  @PathVariable("reviewId") String reviewId,
                                                  @Valid @RequestBody ReviewCreateUpdateRequestDto review,
                                                  @AuthenticationPrincipal Jwt jwt){

        ReviewCreateUpdateRequest reviewCreateUpdateRequest=reviewMapper.toReviewCreateUpdateRequest(review);

        User user=jwtToUser(jwt);

        Review updatedReview= reviewService.updateReview(user,restaurantId,reviewId,reviewCreateUpdateRequest);

        return ResponseEntity.ok(reviewMapper.toDto(updatedReview));

    }

    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("restaurantId") String restaurantId,
                                             @PathVariable("reviewId") String reviewId,
                                             @AuthenticationPrincipal Jwt jwt){

        User user=jwtToUser(jwt);

        reviewService.deleteReview(user,restaurantId,reviewId);

        return ResponseEntity.noContent().build();

    }

    private User jwtToUser(Jwt jwt){
        return User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName((jwt.getClaimAsString("given_name")))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
