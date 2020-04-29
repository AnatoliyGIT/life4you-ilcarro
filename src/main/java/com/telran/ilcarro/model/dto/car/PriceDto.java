package com.telran.ilcarro.model.dto.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDto {
    private String currency;
    private double amount;
}
