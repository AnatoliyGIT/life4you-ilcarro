package com.telran.ilcarro.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HistoryPeriodDto {
    private String order_id;
    private String start_date_time;
    private String end_date_time;
    private boolean paid;
    private double amount;
    private String booking_date;
}
