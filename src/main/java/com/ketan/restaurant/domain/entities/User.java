package com.ketan.restruant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    // keywork type is used for exact matching
    @Field(type = FieldType.Keyword)
    private String id;

    // text type is used when we allow partial matching
    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private  String givenName;

    @Field(type = FieldType.Text)
    private  String familyName;

}
