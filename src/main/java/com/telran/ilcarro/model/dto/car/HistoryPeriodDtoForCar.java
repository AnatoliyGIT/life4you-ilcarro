package com.telran.ilcarro.model.dto.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HistoryPeriodDtoForCar {
    private String order_id;
    private String start_date_time;
    private String end_date_time;
    private boolean paid;
    private double amount;
    private String booking_date;
}
