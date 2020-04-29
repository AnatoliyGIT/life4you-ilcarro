package com.telran.ilcarro.model.dto.car;

import com.telran.ilcarro.model.dto.PersonWhoBookedDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedPeriodForCarDto {
    private String order_id;
    private String start_date_time;
    private String end_date_time;
    private boolean paid;
    private double amount;
    private String booking_date;
    private PersonWhoBookedDto person_who_booked;
}
