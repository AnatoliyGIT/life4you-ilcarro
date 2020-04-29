package com.telran.ilcarro.model.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedCarsDtoForUser {
    private String serial_number;
    private BookedPeriodDto booked_period;
}
