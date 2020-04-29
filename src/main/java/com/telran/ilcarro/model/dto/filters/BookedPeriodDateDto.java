package com.telran.ilcarro.model.dto.filters;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedPeriodDateDto {
    private String start_date_time;
    private String end_date_time;
}
