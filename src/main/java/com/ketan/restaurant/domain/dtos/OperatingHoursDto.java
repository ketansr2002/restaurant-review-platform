package com.ketan.restaurant.domain.dtos;

import com.ketan.restaurant.domain.entities.TimeRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatingHoursDto {


    @Valid
    private TimeRangeDto monday;

    @Valid
    private  TimeRangeDto tuesday;

    @Valid
    private  TimeRangeDto wednesday;

    @Valid
    private  TimeRangeDto thursday;

    @Valid
    private  TimeRangeDto friday;

    @Valid
    private  TimeRangeDto saturday;

    @Valid
    private  TimeRangeDto sunday;

}
