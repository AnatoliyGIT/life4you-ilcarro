package com.telran.ilcarro.controller;


import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.dto.ReservationDto;
import com.telran.ilcarro.model.dto.car.*;
import com.telran.ilcarro.service.ReservationServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@CrossOrigin
@RestController
@RequestMapping
public class ReservationController {
    private ReservationServiceImpl reservationService;

    @Autowired
    public ReservationController(ReservationServiceImpl reserve) {
        this.reservationService = reserve;
    }

    @ApiOperation(value = "make a reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BookedPeriodBaseDto.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with given {id} not found!")
    })
    //@author Dmitry
    @PostMapping("/car/reservation")
    public BookedPeriodBaseDto makeReservation(@RequestParam String serial_number,
                                               @RequestBody ReservationDto dto,
                                               @RequestHeader("Authorization") String token) {
        try {
            return reservationService.makeReservation(serial_number, dto, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @ApiOperation(value = "Payment for reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 403, message = "Error! Forbidden"),
            @ApiResponse(code = 404, message = "Error! Not found"),
    })
    //@author Anatoly
    @GetMapping(value = "/car/payment")
    public void paymentForReservation(@RequestHeader("Authorization") String token
            , @RequestParam String bookedId) {
        try {
            reservationService.bookingPayment(token, bookedId);
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException | RequestArgumentsException | CarDetailsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "reservation cancellation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Car with given {id} not found!")
    })
    //@author Anatoly
    @DeleteMapping("/car/reservation")
    public void reservationCancellation(@RequestParam String serial_number,
                                        @RequestParam String start_date_time,
                                        @RequestHeader("Authorization") String token) {
        try {
            reservationService.cancelReservation(serial_number, start_date_time, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "Lock car for booking by ID (owner)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = OwnerCarDtoForCar.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 403, message = "Error! Forbidden"),
            @ApiResponse(code = 404, message = "Error! Car with given {id} not found!")
    })
    //@author Anatoly
    @PostMapping("/user/reservation")
    public OwnerCarDtoForCar reserveCarByIdForOwner(@RequestParam String serial_number,
                                                    @RequestBody ArrayList<ReservedPeriodDto> dto,
                                                    @RequestHeader("Authorization") String token) {
        try {
            return reservationService.reserveCarByIdForOwner(serial_number, dto, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "Unlock car for booking by ID (owner)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = OwnerCarDtoForCar.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 403, message = "Error! Forbidden"),
            @ApiResponse(code = 404, message = "Error! Car with given {id} not found!")
    })
    //@author Anatoly
    @PostMapping("/user/free")
    public OwnerCarDtoForCar freeCarByIdForOwner(@RequestParam String serial_number,
                                                    @RequestBody ArrayList<ReservedPeriodDto> dto,
                                                    @RequestHeader("Authorization") String token) {
        try {
            return reservationService.freeCarByIdForOwner(serial_number, dto, token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (RequestArgumentsException | RegistrationModelException | TokenValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }
}
