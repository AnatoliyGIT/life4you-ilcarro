package com.telran.ilcarro.controller;

import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.exception.RequestArgumentsException;
import com.telran.ilcarro.model.dto.filters.CarsFiltersDto;
import com.telran.ilcarro.model.dto.filters.PageResponse;
import com.telran.ilcarro.model.dto.filters.PageResponseWithFilter;
import com.telran.ilcarro.service.interfaces.CarFilterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin
@RestController
@RequestMapping
public class CarFilterController {
    private CarFilterService carFilterService;

    @Autowired
    public CarFilterController(CarFilterService carFilterService) {
        this.carFilterService = carFilterService;
    }

    @ApiOperation(value = "Get car by location")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarsFiltersDto[].class),
            @ApiResponse(code = 400, message = "Error! Wrong arguments request!"),
            @ApiResponse(code = 404, message = "Error! Cars by location not found")
    })

    //@author Dmitry
    @GetMapping(value = "/search/geo")
    public PageResponse getAllCarsByLocation(
            @RequestParam(value = "latitude") double latitude
            , @RequestParam(value = "longitude") double longitude
            , @RequestParam(value = "radius") double radius
            , @RequestParam(value = "items_on_page") int items_on_page
            , @RequestParam(value = "current_page") int current_page) {
        try {
            return carFilterService.getAllCarsByLocation(latitude, longitude, radius, items_on_page, current_page);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    @ApiOperation(value = "Get car by date,location and price")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarsFiltersDto[].class),
            @ApiResponse(code = 404, message = "Error! Cars by date and location not found")
    })
    //author Dmitry
    @GetMapping(value = "/search")
    public PageResponse getAllCarsByDateLocation(
            @RequestParam(value = "latitude") double latitude
            , @RequestParam(value = "longitude") double longitude
            , @RequestParam(value = "start_date") String dateStart
            , @RequestParam(value = "end_date") String dateEnd
            , @RequestParam(value = "min_amount") double min_amount
            , @RequestParam(value = "max_amount") double max_amount
            , @RequestParam(value = "ascending") boolean ascending
            , @RequestParam(value = "items_on_page") int items_on_page
            , @RequestParam(value = "current_page") int current_page) {
        try {
            return carFilterService.getAllCarsByDateLocation(
                    latitude
                    , longitude
                    , dateStart,
                    dateEnd,
                    min_amount,
                    max_amount,
                    ascending,
                    items_on_page,
                    current_page);
        } catch (IllegalArgumentException | RequestArgumentsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }


    @ApiOperation(value = "Get car by filter")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarsFiltersDto[].class),
            @ApiResponse(code = 400, message = "Error! Wrong arguments request!"),
            @ApiResponse(code = 404, message = "Error! Cars by filter not found!")
    })
    //@author Dmitry
    @GetMapping(value = "/search/filters")
    public PageResponseWithFilter getAllCarsByFilter(
            @RequestParam(value = "make", required = false) String make
            , @RequestParam(value = "model", required = false) String model
            , @RequestParam(value = "year", required = false) String year
            , @RequestParam(value = "fuel", required = false) String fuel
            , @RequestParam(value = "gear", required = false) String gear
            , @RequestParam(value = "wheels_drive", required = false) String wheels_drive
            , @RequestParam int items_on_page
            , @RequestParam int current_page) {
        try {
            return carFilterService.getAllCarsByFilter(make, model, year, fuel, gear, wheels_drive, items_on_page
                    , current_page);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get filters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Error! Filters not found!")
    })
    @GetMapping(value = "/filters")
    public JsonNode getFilters() {
        try {
            return carFilterService.getFilters();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @ApiOperation(value = "Search that contains all searches")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error! Wrong arguments request!"),
            @ApiResponse(code = 404, message = "Error! Cars by all filters not found!")
    })
    //@author Dmitry
    @GetMapping(value = "/search/all")
    public PageResponseWithFilter getCarsByAllFilters(@RequestParam int current_page, @RequestParam int items_on_page
            , @RequestParam(value = "make", required = false) String make
            , @RequestParam(value = "model", required = false) String model
            , @RequestParam(value = "wheels_drive", required = false) String wheels_drive
            , @RequestParam(value = "year", required = false) String year
            , @RequestParam(value = "gear", required = false) String gear
            , @RequestParam(value = "fuel", required = false) String fuel
            , @RequestParam(value = "engine", required = false) String engine
            , @RequestParam(value = "latitude") double latitude
            , @RequestParam(value = "longitude") double longitude
            , @RequestParam(value = "radius") double radius
            , @RequestParam(value = "start_date") String date_start
            , @RequestParam(value = "end_date") String date_end
            , @RequestParam(value = "min_amount") double min_amount
            , @RequestParam(value = "max_amount") double max_amount
            , @RequestParam(value = "ascending") boolean ascending) {
        try {
            return carFilterService.getCarsByAllFilters(current_page, items_on_page, make
                    , model, wheels_drive, year, gear, fuel, engine, latitude, longitude, radius, date_start
                    , date_end, min_amount, max_amount, ascending);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException | RequestArgumentsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
