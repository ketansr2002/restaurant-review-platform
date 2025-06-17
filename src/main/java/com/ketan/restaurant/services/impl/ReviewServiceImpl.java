package com.ketan.restaurant.services.impl;

import com.ketan.restaurant.domain.ReviewCreateUpdateRequest;
import com.ketan.restaurant.domain.entities.Photo;
import com.ketan.restaurant.domain.entities.Restaurant;
import com.ketan.restaurant.domain.entities.Review;
import com.ketan.restaurant.domain.entities.User;
import com.ketan.restaurant.exceptions.RestaurantNotFoundException;
import com.ketan.restaurant.exceptions.ReviewNotAllowedException;
import com.ketan.restaurant.repositories.RestaurantRepository;
import com.ketan.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review) {

        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExistingReview= restaurant.getReviews()
                .stream().anyMatch(r-> r.getWrittenBy().getId().equals(author.getId()));

        if(hasExistingReview){
            throw new ReviewNotAllowedException("User has already reviewd this restaurant");
        }
        LocalDateTime now=LocalDateTime.now();
        List<Photo> photos=review.getPhotoIds().stream().map(url->{
                    return Photo.builder()
                            .url(url)
                            .uploadDate(now)
                            .build();
                }).toList();

        String reviewId=UUID.randomUUID().toString();
        Review reviewToCreate=Review.builder()
                .id(reviewId)
                .content(review.getContent())
                .rating(review.getRating())
                .datePosted(now)
                .lastEdited(now)
                .writtenBy(author)
                .photos(photos)
                .build();

        restaurant.getReviews().add(reviewToCreate);
        updateRestaurantAverageRating(restaurant);

        Restaurant savedResataurant=restaurantRepository.save(restaurant);



        return getReview(reviewId, savedResataurant)
                .orElseThrow(()-> new RuntimeException("Error retrieving created review"));



    }

    @Override
    public Page<Review> listOfReviews(String restaurantId, Pageable pageable) {
        Restaurant searchedRestaurant = getRestaurantOrThrow(restaurantId);

        List<Review> reviews=searchedRestaurant.getReviews();

        Sort sort=pageable.getSort();

        if(sort.isSorted()) {

            Sort.Order order = sort.iterator().next();

            String property = order.getProperty();

            boolean isAscending = order.getDirection().isAscending();

            Comparator<Review> comparator = switch (property) {

                case "datePosted" -> Comparator.comparing(Review::getDatePosted);

                case "rating" -> Comparator.comparing(Review::getRating);

                default -> Comparator.comparing(Review::getDatePosted);

            };

            reviews.sort(isAscending ? comparator : comparator.reversed());
        }else {
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
        }

        int start=(int) pageable.getOffset();

        if(start>reviews.size()){
            return new PageImpl<>(Collections.emptyList(),pageable, reviews.size());
        }

        int end=Math.min((start+pageable.getPageSize()),reviews.size());

        return new PageImpl<>(reviews.subList(start,end),pageable, reviews.size());

    }

    @Override
    public Optional<Review> getReview(String restaurantId, String reviewId) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);

        return getReview(reviewId, restaurant);
    }

    private static Optional<Review> getReview(String reviewId, Restaurant restaurant) {
        return restaurant.getReviews().stream()
                .filter(r -> reviewId.equals(r.getId()))
                .findFirst();
    }

    @Override
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest review) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);

        String authorId= author.getId();

        Review existingReview=getReview(reviewId,restaurant)
                .orElseThrow(()->new ReviewNotAllowedException("Review does not exist"));

        if(!authorId.equals(existingReview.getWrittenBy().getId())){
            throw new ReviewNotAllowedException("Cannot update with others user's review");
        }

        if(LocalDateTime.now().isAfter(existingReview.getDatePosted().plusHours(48))){
            throw new ReviewNotAllowedException("Cannot update review after 48 hours");
        }

        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        existingReview.setLastEdited(LocalDateTime.now());

        existingReview.setPhotos(review.getPhotoIds()
                .stream()
                .map(photoId->
                        Photo.builder()
                                .url(photoId)
                                .uploadDate(LocalDateTime.now())
                                .build()).toList());

        updateRestaurantAverageRating(restaurant);

        List<Review> updatedReviews=restaurant.getReviews().stream().filter(r->
                !reviewId.equals(r.getId()))
                .collect(Collectors.toList());

        updatedReviews.add(existingReview);
        restaurant.setReviews(updatedReviews);

        restaurantRepository.save(restaurant);

        return existingReview;
    }

    @Override
    public void deleteReview(User user,String restaurantId, String reviewId) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);

        Review reviewToDelete=getReview(reviewId,restaurant).orElseThrow(()-> new ReviewNotAllowedException(("review does not exist")));

        String currentUserId=user.getId();

        if(!currentUserId.equals(reviewToDelete.getWrittenBy().getId())){
            throw new ReviewNotAllowedException("Only author can delete review");
        }

        List<Review> updatedReviews=restaurant.getReviews().stream()
                .filter(review -> !reviewId.equals(review.getId()))
                .toList();

        restaurant.setReviews(updatedReviews);
        updateRestaurantAverageRating(restaurant);
        restaurantRepository.save(restaurant);

    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.
                findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id not found" + restaurantId));
    }


    private void updateRestaurantAverageRating(Restaurant restaurant){
        List<Review> reviews=restaurant.getReviews();

        if(reviews.isEmpty()){
            restaurant.setAverageRating(0.0f);
        }else{
            double averageRating=reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);

            restaurant.setAverageRating((float) averageRating);
        }
    }
}
