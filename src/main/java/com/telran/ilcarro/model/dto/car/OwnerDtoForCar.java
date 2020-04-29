package com.telran.ilcarro.model.dto.car;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OwnerDtoForCar {
    private String first_name;
    private String second_name;
    private String registration_date;
}
