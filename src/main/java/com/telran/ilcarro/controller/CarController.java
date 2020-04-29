package com.telran.ilcarro.controller;


import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.dto.car.*;
import com.telran.ilcarro.service.interfaces.CarService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping
public class CarController {
    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @ApiOperation(value = "owner get car by serial_number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with give serial number not found!")
    })
    //@author Dmitry
    @GetMapping("/user/cars/car")
    public OwnerCarDtoForCar getOwnerCarById(@RequestParam("serial_number") String serial_number
            , @RequestHeader("Authorization") String token) {
        try {
            return carService.getOwnerCarById(serial_number, token);
        } catch (RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get car by id for users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Error! Car with give serial number not found!")
    })
    //@author Dmitry
    @GetMapping("/car")
    public CarForUsersDto getCarByIdForUsers(@RequestParam("serial_number") String serial_number) {
        try {
            return carService.getCarByIdForUsers(serial_number);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (CarDetailsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    @ApiOperation(value = "Car update")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with give serial number not found!")
    })
    //@Anatoly
    @PutMapping("/car")
    public CarFullDto updateCar(@RequestParam String serial_number,
                                @RequestBody CarFullUploadRequestDto dto,
                                @RequestHeader("Authorization") String token) {
        try {
            return carService.updateCar(serial_number, dto, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (CarDetailsException | RegistrationModelException | RequestArgumentsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (CarAlreadyExistException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @ApiOperation(value = "Car delete")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with give serial number not found!")
    })
    //@author Dmitry
    @DeleteMapping(value = "/car")
    public void deleteCar(@RequestParam(value = "serial_number") String serial_number, @RequestHeader("Authorization") String token) {
        try {
            carService.deleteCarBySerialNumber(serial_number, token);
        } catch (RegistrationModelException | TokenValidationException | CarDetailsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "owner get booked periods by car id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with give serial number not found!")
    })
    @GetMapping("/user/cars/periods")
    public Iterable<BookedPeriodForCarDto> getBookedPeriodsCarById(@RequestParam String serial_number, @RequestHeader("Authorization") String token) {
        try {
            return carService.getBookedPeriodsCarById(serial_number, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (CarDetailsException | TokenValidationException | RegistrationModelException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @ApiOperation(value = "owner get cars")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 404, message = "Error! Cars not found!")
    })
    //@author Dmitry
    @GetMapping("/user/cars")
    public Iterable<OwnerCarDtoForCar> getOwnerCars(@RequestHeader("Authorization") String token) {
        try {
            return carService.getOwnerCars(token);
        } catch (RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @ApiOperation(value = "Add new car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarFullDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization")
    })
    //@Anatoly
    @PostMapping(value = "/car")
    public CarFullDto addCarByOwner(@RequestBody CarFullUploadRequestDto dto, @RequestHeader("Authorization") String token) {
        try {
            return carService.addCarByOwner(token, dto);
        } catch (IllegalArgumentException | CarDetailsException | RegistrationModelException
                | TokenValidationException | RequestArgumentsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (CarAlreadyExistException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get 3 best booked cars")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarForUsersDto[].class),
            @ApiResponse(code = 404, message = "Error! NOT FOUND!")
    })
    @GetMapping(value = "/car/best")
    //@author Dmitry
    public Iterable<CarForUsersDto> getThreePopularCars() {
        try {
            return carService.getThreePopularsCar();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get last order for user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CarLastOrderDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with given {id} not found!")
    })
    //@author Anatoly
    @GetMapping("/car/lastorder")
    public CarLastOrderDto lastBookedCarByUser(@RequestHeader("Authorization") String token) {
        try {
            return carService.getLastOrder(token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get top 5 favorites booked cars for user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 404, message = "Error! User not found"),
            @ApiResponse(code = 401, message = "Error! User not authorized")
    })
    @GetMapping(value = "/car/favorites")
    public Iterable<CarFullDto> getFavoritesCars(@RequestHeader("Authorization") String token) {
        try {
            return carService.getFiveFavoritesCars(token);
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
