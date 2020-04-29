package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CarCustomRepositoryImpl implements CarCustomRepository {
    MongoTemplate mongoTemplate;

    @Autowired
    public CarCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Car> findCarsByOwnerEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("owner.email").is(email));
        return mongoTemplate.find(query, Car.class);
    }

    @Override
    public List<Car> getThreePopularsCar() {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("statistics"))).limit(3);
        return mongoTemplate.find(query, Car.class);
    }

    @Override
    public List<Car> getCarsByNumberList(List<String> numbers) {
        List<Car> cars = new ArrayList<>();
        for (String number : numbers) {
            Query query = new Query();
            query.addCriteria(Criteria.where("serial_number").is(number));
            Car car = mongoTemplate.findOne(query, Car.class);
            if (car != null) {
                cars.add(car);
            }
        }
        return cars;
    }
}
