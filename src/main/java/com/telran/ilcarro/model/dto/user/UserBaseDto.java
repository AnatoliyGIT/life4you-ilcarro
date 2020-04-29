package com.telran.ilcarro.model.dto.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserBaseDto {
    @NotNull
    @Size(min = 1, max = 20)
    private String first_name;
    @NotNull
    @Size(min = 1, max = 30)
    private String second_name;
    @NotNull
    private String photo;
}
