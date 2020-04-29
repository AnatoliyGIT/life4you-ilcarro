package com.telran.ilcarro.model.documents;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BookedCars {
    private String serial_number;
    private BookedPeriod booked_period;
}
