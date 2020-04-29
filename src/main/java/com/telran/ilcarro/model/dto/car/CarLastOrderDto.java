package com.telran.ilcarro.model.dto.car;

import com.telran.ilcarro.model.dto.PersonWhoBookedDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarLastOrderDto {
    private BookedPeriodDateDto booked_periods;
    private PersonWhoBookedDto person_who_booked;
    private CarFullDto car;
}
