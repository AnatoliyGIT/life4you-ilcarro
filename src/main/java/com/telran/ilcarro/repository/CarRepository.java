package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car,String>,CarCustomRepository {
    Car save (Car dto);
}
