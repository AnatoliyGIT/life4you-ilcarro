package com.telran.ilcarro.model.dto.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookedPeriodDateDto {
    private String start_date_time;
    private String end_date_time;
}
