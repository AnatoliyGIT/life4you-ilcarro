package com.telran.ilcarro.model.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerDtoForUser {
    private String email;
    private String first_name;
    private String second_name;
    private String registration_date;
}
