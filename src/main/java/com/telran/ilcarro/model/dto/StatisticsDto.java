package com.telran.ilcarro.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDto {
    private String trips;
    private String rating;
}
