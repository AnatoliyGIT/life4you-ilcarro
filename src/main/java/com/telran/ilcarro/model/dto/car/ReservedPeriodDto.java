package com.telran.ilcarro.model.dto.car;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReservedPeriodDto {
    private String start_date_time;
    private String end_date_time;
}
