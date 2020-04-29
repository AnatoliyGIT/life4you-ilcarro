package com.telran.ilcarro.model.dto.filters;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OwnerDto {
    private String first_name;
    private String second_name;
    private String registration_date;
}
