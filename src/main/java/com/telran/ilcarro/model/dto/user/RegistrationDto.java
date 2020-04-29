package com.telran.ilcarro.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegistrationDto {
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 20)
    private String first_name;
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 30)
    private String second_name;
}
