package com.telran.ilcarro.model.dto.car;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HistoryDtoForCar {
    private String serial_number;
    private HistoryPeriodDtoForCar booked_period;
}
