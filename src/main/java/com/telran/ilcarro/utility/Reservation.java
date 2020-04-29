package com.telran.ilcarro.utility;

import com.telran.ilcarro.model.documents.BookedPeriod;
import com.telran.ilcarro.model.documents.Car;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Reservation {
    @NonNull
    private String serialNumber;
    @NonNull
    private String ownerEmail;
    @NonNull
    private String userEmail;
    @NonNull
    private BookedPeriod bookedPeriod;
}
