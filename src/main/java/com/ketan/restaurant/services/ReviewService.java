package com.ketan.restaurant.services;

import com.ketan.restaurant.domain.ReviewCreateUpdateRequest;
import com.ketan.restaurant.domain.entities.Review;
import com.ketan.restaurant.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {

    Review createReview(User author, String restaurantId , ReviewCreateUpdateRequest review);
    Page<Review> listOfReviews(String restaurantId, Pageable pageable);

    Optional<Review> getReview(String restaurantId,String reviewId);

    Review updateReview(User author,String restaurantId, String reviewId, ReviewCreateUpdateRequest request);

    void deleteReview(User user,String restaurantId,String reviewId);
}
