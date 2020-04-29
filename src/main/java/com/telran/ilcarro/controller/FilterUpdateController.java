package com.telran.ilcarro.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.service.interfaces.FilterUpdateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping
public class FilterUpdateController {
    private FilterUpdateService filterUpdateService;

    @Autowired
    public FilterUpdateController(FilterUpdateService filterUpdateService) {
        this.filterUpdateService = filterUpdateService;
    }

    @ApiOperation(value = "Update timer filters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Error! Filters not found!")
    })
    @PostMapping(value = "filter/update")
    public void changeTimeUpdateFilters(){
        try {
            filterUpdateService.changeTimeUpdateFilters();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
