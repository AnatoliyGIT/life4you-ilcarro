package com.telran.ilcarro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.repository.FilterUpdateRepository;
import com.telran.ilcarro.service.interfaces.FilterUpdateService;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class FilterUpdateServiceImpl implements FilterUpdateService {
    private FilterUpdateRepository filterUpdateRepository;
    private final MongoTemplate mongoTemplate;
    private double currentMinute;
    private Timer timer;
    boolean flag;

    @Autowired
    public FilterUpdateServiceImpl(FilterUpdateRepository filterUpdateRepository, MongoTemplate mongoTemplate) {
        this.filterUpdateRepository = filterUpdateRepository;
        this.mongoTemplate = mongoTemplate;
        currentMinute = 60;
        timer = new Timer("Timer");
        flag = false;
    }

    @Override
    public void changeTimeUpdateFilters() {
//        currentMinute = minute;
        flag = true;
        updateFilters();
        runUpdateFilters();
    }

    @PostConstruct
    public void runUpdateFilters() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                updateFilters();
                if (flag) {
                    timer.cancel();
                    flag = false;
                    timer = new Timer("Timer");
                    runUpdateFilters();
                }
            }
        };
        long delay = 60000;
        long period = (long) (currentMinute * 60 * 1000);
        timer.scheduleAtFixedRate(repeatedTask, delay, period);


        // every night at 2am run your task
//        Calendar today = Calendar.getInstance();
//        today.set(Calendar.HOUR_OF_DAY, 2);
//        today.set(Calendar.MINUTE, 0);
//        today.set(Calendar.SECOND, 0);
//
//        Timer timer = new Timer();
//        timer.schedule(repeatedTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
    }


    @Override
    public void updateFilters() {
        Utils.usageStatistics("General", "updateTimerFilters");
        List<Car> list = mongoTemplate.findAll(Car.class);
        String[] parameters = {"make", "model", "year", "engine",
                "fuel", "gear", "wheels_drive", "horsepower",
                "fuel_consumption"};
        String res = "{\"key\" : \"root\"" + getFilter(list, parameters, 0) + "}";
        // create object mapper
        ObjectMapper mapper = new ObjectMapper();
        // convert to json object
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(res);
        } catch (JsonProcessingException e) {
            throw new NotFoundException("filters not found!");
        }
        filterUpdateRepository.save(Filter.builder().id("root").filter_string(jsonNode.toString()).build());
    }

    private String getFilter(List<Car> cars, String[] parameters, int index) {
        if (parameters.length <= index) return "";
        String res = ",";
        Map<Object, List<Car>> map = new TreeMap<>();
        for (Car car : cars) {
            Object value = "";
            switch (parameters[index]) {
                case "make":
                    value = car.getMake();
                    break;
                case "model":
                    value = car.getModel();
                    break;
                case "year":
                    value = car.getYear();
                    break;
                case "engine":
                    value = car.getEngine();
                    break;
                case "fuel":
                    value = car.getFuel();
                    break;
                case "gear":
                    value = car.getGear();
                    break;
                case "wheels_drive":
                    value = car.getWheels_drive();
                    break;
                case "horsepower":
                    value = car.getHorse_power();
                    break;
                case "fuel_consumption":
                    value = car.getFuel_consumption();
                    break;
            }
            if (!map.containsKey(value)) {
                map.put(value, new ArrayList<>());
            }
            map.get(value).add(car);
        }
        res += "\"" + parameters[index] + "\" : [";
        boolean flag = false;
        for (Object key : map.keySet()) {
            if (flag) res += ",";
            if (parameters[index].equals("horsepower") || parameters[index].equals("fuel_consumption")) {
                res += "{\"key\" : " + key + getFilter(map.get(key), parameters, index + 1) + "}";
            } else {
                res += "{\"key\" : " + "\"" + key + "\"" + getFilter(map.get(key), parameters, index + 1) + "}";
            }

            flag = true;
        }
        res += "]";
        return res;
    }
}
