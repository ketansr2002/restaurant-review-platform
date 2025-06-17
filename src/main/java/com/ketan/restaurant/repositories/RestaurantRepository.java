package com.ketan.restaurant.repositories;

import com.ketan.restaurant.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends ElasticsearchRepository<Restaurant,String> {

    // we hace to  create some custom queries
    Page<Restaurant> findByAverageRatingGreaterThanEqual(Float minRating, Pageable pageable);

    //this is query implementing fuzzy matching using name, cuisine type etc
    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"range\": {\"averageRating\": {\"gte\": ?1}}}" +
            "    ]," +
            "    \"should\": [" +
            "      {\"fuzzy\": {\"name\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}," +
            "      {\"fuzzy\": {\"cuisineType\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "    ]," +
            "    \"minimum_should_match\": 1" +
            "  }" +
            "}" +
            "}")
    Page<Restaurant> findByQueryAndMinRating(String query, Float minRating, Pageable pageable);

    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"geo_distance\": {" +
            "       \"distance\": \"?2km\"," +
            "        \"geoLocation\": {" +
            "          \"lat\": ?0," +
            "          \"lon\": ?1" +
            "        }" +
            "      }}" +
            "    ]" +
            "  }" +
            "}")
    Page<Restaurant> findByLocationNear(
            Float latitude,
            Float longitude,
            Float radiusKm,
            Pageable pageable);




}
