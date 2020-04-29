package com.telran.ilcarro.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistoryCars {
    private String order_id;
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
    private boolean paid;
    private double amount;
    private String booking_date;
    private PersonWhoBooked person_who_booked;
}
