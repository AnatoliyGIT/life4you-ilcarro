package com.telran.ilcarro.model.dto.car;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedPeriodBaseDto {
    private String order_number;
    private double amount;
    private String booking_date;
}
