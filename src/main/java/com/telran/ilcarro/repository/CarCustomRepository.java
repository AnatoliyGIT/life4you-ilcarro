package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.Car;

import java.util.List;

public interface CarCustomRepository {
    List<Car> findCarsByOwnerEmail(String email);
    List<Car> getThreePopularsCar();
    List<Car> getCarsByNumberList(List<String> numbers);
}
