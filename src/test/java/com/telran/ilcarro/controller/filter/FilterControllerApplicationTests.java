package com.telran.ilcarro.controller.filter;

import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.model.documents.Car;
import com.telran.ilcarro.repository.CarFilterRepository;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.service.interfaces.CarFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FilterControllerApplicationTests {
private CarFilterRepository carFilterRepository;
private CarFilterService carFilterService;
private CarRepository carRepository;
private List<Car> cars;

@Autowired
    public FilterControllerApplicationTests(CarFilterRepository carFilterRepository
            , CarFilterService carFilterService
    ,CarRepository carRepository) {
        this.carFilterRepository = carFilterRepository;
        this.carFilterService = carFilterService;
        this.carRepository = carRepository;
    }

    @BeforeEach
    void setUp() {
    cars = new ArrayList<>();
    cars = carRepository.findAll();
    }
    //
    /*///////////////////////////////////////////////////////////////////////////////////////////*/
    @Test
    void whenAssertNotNullTest() {
        assertNotNull(cars);
    }

    //getAllCarsByFilter
    /*///////////////////////////////////////////////////////////////////////////////////////////*/
    @Test
    void whenAssertNotFoundExcGetAllCarsByFilter() {
        assertThrows(NotFoundException.class, () -> {
            carFilterService.getAllCarsByFilter("Make","Model","2015"
                    ,"fuel","gear","wheels",5,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenCurrentPageLess1GetAllCarsByFilter() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByFilter("Make","Model","2015"
                    ,"fuel","gear","wheels",5,-1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenItemsOnPageLess1GetAllCarsByFilter() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByFilter("Make","Model","2015"
                    ,"fuel","gear","wheels",0,1);
        });
    }

    //getAllCarsByLocation
    /*///////////////////////////////////////////////////////////////////////////////////////////*/
    @Test
    void whenAssertNotFoundExcGetAllCarsByLocation() {
        assertThrows(NotFoundException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,500
                    ,5,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenCurrentPageLess1GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,500
                    ,5,-1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenItemsOnPageLess1GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,500
                    ,-1,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenItemsOnPageMoreIntegerMaxGetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,500
                    ,Integer.MAX_VALUE,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenRadiusLess0GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,-1
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenRadiusMoreOrEqualsMaxDoubleGetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,Double.MAX_VALUE
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenRadiusIsNanGetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.000000,35.00000,Double.POSITIVE_INFINITY - Double.POSITIVE_INFINITY
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenLatitudeEquals0GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(0,35.00000,500
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenLongitudeEquals0GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.0000,0,500
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenLatitudeIsNanGetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(Double.POSITIVE_INFINITY - Double.POSITIVE_INFINITY,35.00000,500
                    ,10,1);
        });
    }

    @Test
    void whenAssertIllegalArgumentExceptionThenLongitudeIsNan0GetAllCarsByLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            carFilterService.getAllCarsByLocation(34.0000,Double.POSITIVE_INFINITY - Double.POSITIVE_INFINITY,500
                    ,10,1);
        });
    }

    //getAllCarsByDateLocation
    /*///////////////////////////////////////////////////////////////////////////////////////////*/


    //getCarsByAllFilters
    /*///////////////////////////////////////////////////////////////////////////////////////////*/


    //getFilters
    /*///////////////////////////////////////////////////////////////////////////////////////////*/
}
