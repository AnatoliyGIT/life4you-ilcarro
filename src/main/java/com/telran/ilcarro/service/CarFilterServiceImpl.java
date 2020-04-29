package com.telran.ilcarro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.model.documents.Car;
import com.telran.ilcarro.model.dto.filters.CarPage;
import com.telran.ilcarro.model.dto.filters.PageResponse;
import com.telran.ilcarro.model.dto.filters.PageResponseWithFilter;
import com.telran.ilcarro.repository.CarFilterRepository;
import com.telran.ilcarro.service.interfaces.CarFilterService;
import com.telran.ilcarro.utility.ConvertersFilters;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class CarFilterServiceImpl implements CarFilterService {
    private CarFilterRepository carFilterRepository;

    @Autowired
    public CarFilterServiceImpl(CarFilterRepository carFilterRepository) {
        this.carFilterRepository = carFilterRepository;
    }

    //@author Dmitry
    @Override
    public PageResponse getAllCarsByLocation(double latitude, double longitude, double radius
            , int items_on_page, int current_page) {
        Utils.usageStatistics("General", "getCarByLocation");
        validationPages(items_on_page, current_page);
        validationLocation(latitude, longitude, radius);
//        int currentPage = current_page == 1 ? 0 : current_page - 1;
        Page pageTotal = carFilterRepository.getAllCarsByLocation(latitude, longitude
                , radius, Integer.MAX_VALUE, 0);
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getAllCarsByLocation(latitude, longitude
                    , radius * 2, Integer.MAX_VALUE, 0);
        }
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getAllCarsByLocation(latitude, longitude
                    , radius * 4, Integer.MAX_VALUE, 0);
        }
        if (pageTotal.getTotalPages() == 0) {
            throw new NotFoundException("No cars on current page!");
        }
//        if (items_on_page >= pageTotal.getTotalElements()) {
//            items_on_page = (int) pageTotal.getTotalElements();
//        }
//        String totalElements = Long.toString(pageTotal.getTotalElements());
//        Page page = carFilterRepository.getAllCarsByLocation(latitude, longitude
//                , radius, items_on_page, currentPage);
//        if (page.getTotalElements() == 0)
//            throw new NotFoundException("No cars on current page!");
        List<Car> cars = new ArrayList<>();
        cars.addAll(pageTotal.getContent());
        CarPage carPage = pagination(current_page, items_on_page, cars);
        return PageResponse.builder()
                .cars(ConvertersFilters.createCarForUsersDtoListFromCarList(carPage.getCars()))
                .current_page(String.valueOf(carPage.getCurrentPage()))
                .items_on_page(String.valueOf(carPage.getItemsOnPage()))
                .items_total(String.valueOf(carPage.getTotalItems()))
                .build();
    }


    //@author Dmitry
    @Override
    public PageResponse getAllCarsByDateLocation(double latitude, double longitude, String date_start,
                                                 String date_end, double min_amount, double max_amount,
                                                 boolean ascending, int items_on_page, int current_page) {
        Utils.usageStatistics("General", "getCarByDateLocationAndPrice");
        if (date_start == null || date_end == null)
            throw new IllegalArgumentException("Invalid start or end date");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        date_start = Utils.correctionDateAndTime(date_start);
        date_end = Utils.correctionDateAndTime(date_end);
        Utils.isValidFormatDateAndTime(date_start, date_end);
        LocalDateTime dateStart = LocalDateTime.parse(date_start, format);
        LocalDateTime dateEnd = LocalDateTime.parse(date_end, format);
        if (dateStart.isBefore(LocalDateTime.now().plusHours(2))
                || dateEnd.isBefore(LocalDateTime.now().plusHours(2)))
            throw new IllegalArgumentException("Yesterday date and time!");
        if (dateStart.isAfter(dateEnd))
            throw new IllegalArgumentException("Invalid start or end date");
        if (max_amount < min_amount || min_amount < 0)
            throw new IllegalArgumentException("The minimum price is greater than the maximum or min_amount less zero!");
        validationPages(items_on_page, current_page);
        validationLocationWithOutRadius(latitude, longitude);
//        int currentPage = current_page == 1 ? 0 : current_page - 1;
        Page pageTotal = carFilterRepository.getAllCarsByDateLocation(1500, latitude, longitude, dateStart,
                dateEnd, min_amount, max_amount, ascending, Integer.MAX_VALUE, 0);
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getAllCarsByDateLocation(3000, latitude, longitude, dateStart,
                    dateEnd, min_amount, max_amount, ascending, Integer.MAX_VALUE, 0);
        }
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getAllCarsByDateLocation(6000, latitude, longitude, dateStart,
                    dateEnd, min_amount, max_amount, ascending, Integer.MAX_VALUE, 0);
        }
        if (pageTotal.getTotalPages() == 0) {
            throw new NotFoundException("No cars on current page!");
        }
//        if (items_on_page >= pageTotal.getTotalElements()) {
//            items_on_page = (int) pageTotal.getTotalElements();
//        }
//        String totalElements = Long.toString(pageTotal.getTotalElements());
//        Page page = carFilterRepository.getAllCarsByDateLocation(latitude, longitude, dateStart,
//                dateEnd, min_amount, max_amount, ascending, items_on_page, currentPage);
//        if (page.getTotalElements() == 0)
//            throw new NotFoundException("Cars not founds!");
        List<Car> cars = new ArrayList<>();
        cars.addAll(pageTotal.getContent());
        cars.removeIf(car -> !Utils.validationBookedPeriods(dateStart, dateEnd, car)); // Booking validator
        CarPage carPage = pagination(current_page, items_on_page, cars);
        return PageResponse.builder()
                .cars(ConvertersFilters.createCarForUsersDtoListFromCarList(carPage.getCars()))
                .current_page(String.valueOf(carPage.getCurrentPage()))
                .items_on_page(String.valueOf(carPage.getItemsOnPage()))
                .items_total(String.valueOf(carPage.getTotalItems()))
                .build();
    }

    /*
     *@author Dmitry Asmalouski
     */
    @Override
    public PageResponseWithFilter getAllCarsByFilter(String make, String model, String year, String fuel
            , String gear, String wheels_drive, int items_on_page, int current_page) {
        Utils.usageStatistics("General", "getCarByFilter");
        if (current_page < 1 || items_on_page < 1) throw new IllegalArgumentException("Invalid current page!");
//        int currentPage = current_page == 1 ? 0 : current_page - 1;
        Page pageTotal = carFilterRepository.getAllCarsByFilter(make, model, year, fuel
                , gear, wheels_drive, Integer.MAX_VALUE, 0);
        if (pageTotal.getTotalPages() == 0) {
            throw new NotFoundException("No cars on current page!");
        }
//        if (items_on_page >= pageTotal.getTotalElements()) {
//            items_on_page = (int) pageTotal.getTotalElements();
//        }
//        String totalElements = Long.toString(pageTotal.getTotalElements());
//        Page page = carFilterRepository.getAllCarsByFilter(make, model, year, fuel
//                , gear, wheels_drive, items_on_page, currentPage);
//        if (page.getTotalElements() == 0) throw new NotFoundException("No cars on current page!");
        List<Car> cars = new ArrayList<>();
        cars.addAll(pageTotal.getContent());
        CarPage carPage = pagination(current_page, items_on_page, cars);
        return PageResponseWithFilter.builder()
                .cars(ConvertersFilters.createCarForUsersDtoListFromCarList(carPage.getCars()))
                .current_page(String.valueOf(carPage.getCurrentPage()))
                .items_on_page(String.valueOf(carPage.getItemsOnPage()))
                .items_total(String.valueOf(carPage.getTotalItems()))
                .filter(getFilters())
                .build();
    }

    @Override
    //@author Dmitry Asmalouski
    public PageResponseWithFilter getCarsByAllFilters(int current_page, int items_on_page, String make
            , String model, String wheels_drive, String year, String gear
            , String fuel, String engine, double latitude, double longitude
            , double radius, String dateStart
            , String dateEnd, double min_amount, double max_amount
            , boolean ascending) {
        Utils.usageStatistics("General", "searchThatContainsAllSearches");
        dateStart = Utils.correctionDateAndTime(dateStart);
        dateEnd = Utils.correctionDateAndTime(dateEnd);
        Utils.isValidFormatDateAndTime(dateStart, dateEnd);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date_start = LocalDateTime.parse(dateStart, format);
        LocalDateTime date_end = LocalDateTime.parse(dateEnd, format);

        if (date_start.plusMinutes(1).isBefore(LocalDateTime.now().plusHours(2))
                || date_end.plusMinutes(1).isBefore(LocalDateTime.now().plusHours(2)))
            throw new IllegalArgumentException("Yesterday date and time!");
        if (date_start.isAfter(date_end))
            throw new IllegalArgumentException("Invalid start or end date");
        if (longitude > 180D || longitude < -180D) throw new IllegalArgumentException("Invalid longitude!");
        if (latitude > 80D || latitude < -80D) throw new IllegalArgumentException("Invalid latitude!");
        if (max_amount < min_amount || min_amount < 0)
            throw new IllegalArgumentException("The minimum price is greater than the maximum or min_amount less zero!");
        if (current_page < 1 || items_on_page < 1) throw new IllegalArgumentException("Invalid current page!");
//        int currentPage = current_page == 1 ? 0 : current_page - 1;
        Page pageTotal = carFilterRepository.getCarsByAllFilters(0, Integer.MAX_VALUE, make
                , model, wheels_drive, year, gear, fuel, engine, latitude, longitude, radius, date_start
                , date_end, min_amount, max_amount, ascending);
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getCarsByAllFilters(0, Integer.MAX_VALUE, make
                    , model, wheels_drive, year, gear, fuel, engine, latitude, longitude, radius * 2, date_start
                    , date_end, min_amount, max_amount, ascending);
        }
        if (pageTotal.getTotalPages() == 0) {
            pageTotal = carFilterRepository.getCarsByAllFilters(0, Integer.MAX_VALUE, make
                    , model, wheels_drive, year, gear, fuel, engine, latitude, longitude, radius * 4, date_start
                    , date_end, min_amount, max_amount, ascending);
        }
        if (pageTotal.getTotalPages() == 0) {
            throw new NotFoundException("No cars on current page!");
        }
        if (items_on_page >= pageTotal.getTotalElements()) {
            items_on_page = (int) pageTotal.getTotalElements();
        }
//        String totalElements = Long.toString(pageTotal.getTotalElements());
//        Page page = carFilterRepository.getCarsByAllFilters(currentPage, items_on_page, make
//                , model, wheels_drive, year, gear, fuel, engine, latitude, longitude, radius, date_start
//                , date_end, min_amount, max_amount, ascending);
//        if (page.getTotalElements() == 0) throw new NotFoundException("Cars not founds!");
        List<Car> cars = new ArrayList<>();
        cars.addAll(pageTotal.getContent());
        cars.removeIf(car -> !Utils.validationBookedPeriods(date_start, date_end, car)); //Booking validator
        CarPage carPage = pagination(current_page, items_on_page, cars);
        return PageResponseWithFilter.builder()
                .cars(ConvertersFilters.createCarForUsersDtoListFromCarList(carPage.getCars()))
                .current_page(String.valueOf(carPage.getCurrentPage()))
                .items_on_page(String.valueOf(carPage.getItemsOnPage()))
                .items_total(String.valueOf(carPage.getTotalItems()))
                .filter(getFilters())
                .build();
    }

    //@authors Damir, Dmitry
    @Override
    public JsonNode getFilters() {
        try {
            JsonNode jsonNode = carFilterRepository.getFilters();
            //filterRepository.save(Filter.builder().id("root").filter_string(jsonNode.toString()).build());
            Utils.usageStatistics("General", "getFilters");
            return jsonNode;
        } catch (JsonProcessingException e) {
            throw new NotFoundException("Filters not found!");
        }
    }

    private void validationPages(int items_on_page, int current_page) {
        if (current_page < 1 || items_on_page < 1 || items_on_page >= Integer.MAX_VALUE)
            throw new IllegalArgumentException("Invalid current page!");
    }


    private void validationLocation(double latitude, double longitude, double radius) {
        if (latitude < -80D || longitude < -180D || latitude > 80D
                || Double.isNaN(latitude) || longitude > 180D || Double.isNaN(longitude))
            throw new IllegalArgumentException("Invalid latitude or longitude!");
        if (radius <= 0 || radius >= Double.MAX_VALUE || Double.isNaN(radius))
            throw new IllegalArgumentException("Invalid radius!");
    }

    private void validationLocationWithOutRadius(double latitude, double longitude) {
        if (latitude < -80D || longitude < -180D || latitude > 80D
                || Double.isNaN(latitude) || longitude > 180D || Double.isNaN(longitude))
            throw new IllegalArgumentException("Invalid latitude or longitude!");
    }

    private CarPage pagination(int currentPage, int itemsOnPage, List<Car> cars) {
        int size = cars.size();
        if ((currentPage * itemsOnPage) > size && (currentPage * itemsOnPage) >= size + itemsOnPage) {
            throw new NotFoundException("No cars on current page!");
        }
        int x = currentPage * itemsOnPage;
        if (x >= size) {
            x = size;
        }
        ArrayList<Car> list = new ArrayList<>();
        for (int i = (currentPage * itemsOnPage) - itemsOnPage; i < x; i++) {
            list.add(cars.get(i));
        }
        return CarPage.builder()
                .cars(list)
                .currentPage(currentPage)
//                .itemsOnPage(list.size())
                .itemsOnPage(itemsOnPage)
                .totalItems(size)
                .build();
    }
}
