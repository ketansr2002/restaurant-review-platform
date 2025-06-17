package com.ketan.restaurant.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.DataOutput;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoPointDto {

    private Double latitude;
    private Double longitude;

}
