package com.telran.ilcarro.service.interfaces;

import com.telran.ilcarro.model.documents.BookedPeriod;
import com.telran.ilcarro.model.dto.ReservationDto;
import com.telran.ilcarro.model.dto.car.*;

import java.util.ArrayList;

public interface ReservationService {
    BookedPeriodBaseDto makeReservation(String serial_number, ReservationDto dto, String token);

    void cancelReservation(String serial_number, String start_date_time, String token);

    void bookingTimer(String serialNumber, String userEmail, String ownerEmail, BookedPeriod bookedPeriod);

    void bookingPayment(String token, String bookedId);

    OwnerCarDtoForCar reserveCarByIdForOwner(String serial_number, ArrayList<ReservedPeriodDto> dto, String token);

    OwnerCarDtoForCar freeCarByIdForOwner(String serial_number, ArrayList<ReservedPeriodDto> dto, String token);
}
