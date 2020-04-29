package com.telran.ilcarro.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistoryDtoForUser {
    private String serial_number;
    private HistoryPeriodDto booked_period;
}
