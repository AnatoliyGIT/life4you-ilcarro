package com.telran.ilcarro.model.dto.car;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedCarsDtoForCar {
    private String serial_number;
    private BookedPeriodForCarDto booked_period;
}
