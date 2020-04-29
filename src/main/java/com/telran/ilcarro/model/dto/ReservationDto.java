package com.telran.ilcarro.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDto {
    private String start_date_time;
    private String end_date_time;
    private PersonWhoBookedDto person_who_booked;
}

