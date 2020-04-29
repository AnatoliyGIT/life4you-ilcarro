package com.telran.ilcarro.service.interfaces;

import com.telran.ilcarro.model.dto.car.*;

public interface CarService {
    OwnerCarDtoForCar getOwnerCarById(String serial_number, String token);

    CarFullDto addCarByOwner(String token, CarFullUploadRequestDto carFullDto);

    Iterable<OwnerCarDtoForCar> getOwnerCars(String token);

    CarFullDto updateCar(String serial_number, CarFullUploadRequestDto dto, String token);

    void deleteCarBySerialNumber(String serial_number, String token);

    Iterable<BookedPeriodForCarDto> getBookedPeriodsCarById(String serial_number, String token);

    CarForUsersDto getCarByIdForUsers(String serial_number);

    Iterable<CarForUsersDto> getThreePopularsCar();

    CarLastOrderDto getLastOrder(String token);

    Iterable<CarFullDto> getFiveFavoritesCars(String token);
}
