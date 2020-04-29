package com.telran.ilcarro.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonWhoBookedDto {
    private String email;
    private String first_name;
    private String second_name;
    private String phone;
}
