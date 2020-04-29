package com.telran.ilcarro.service.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.telran.ilcarro.model.dto.filters.PageResponse;
import com.telran.ilcarro.model.dto.filters.PageResponseWithFilter;

public interface CarFilterService {
    PageResponse getAllCarsByLocation(double latitude, double longitude, double radius
            , int items_on_page, int current_page);

    PageResponse getAllCarsByDateLocation(
            double latitude, double longitude,
            String dateStart,
            String dateEnd,
            double min_amount,
            double max_amount,
            boolean ascending,
            int items_on_page,
            int current_page);

    PageResponseWithFilter getAllCarsByFilter(String make, String model, String year, String fuel
            , String gear, String wheels_drive, int items_on_page
            , int current_page);

    PageResponseWithFilter getCarsByAllFilters(int current_page, int items_on_page, String make,
                                               String model, String wheels_drive, String year,
                                               String gear, String fuel, String engine, double latitude,
                                               double longitude, double radius
            , String dateStart, String dateEnd, double min_amount
            , double max_amount, boolean ascending);

    JsonNode getFilters();
}
