package com.ketan.restaurant.domain.entities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "restraunt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    // we have added this id annotation on this entity only because it has @Document annotation
    @Id
    private String id;

    @Field(type =  FieldType.Text )
    private String name;

    @Field(type =  FieldType.Text )
    private String cuisineType;

    @Field(type =  FieldType.Keyword )
    private String contactInformation;

    @Field(type =  FieldType.Float )
    private Float averageRating;

    @GeoPointField
    private GeoPoint geoLocation;

    @Field(type =  FieldType.Nested)
    private Address address;

    @Field(type =  FieldType.Nested)
    private OperatingHours operatingHours;

    @Field(type =  FieldType.Nested)
    private List<Photo> photos=new ArrayList<>();

    @Field(type =  FieldType.Nested)
    private List<Review> reviews=new ArrayList<>();

    @Field(type =  FieldType.Nested)
    private User createdBy;


}
