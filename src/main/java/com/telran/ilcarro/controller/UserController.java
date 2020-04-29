package com.telran.ilcarro.controller;

import com.itextpdf.text.DocumentException;
import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.dto.car.CarFullDto;
import com.telran.ilcarro.model.dto.user.BookedCarsDtoForUser;
import com.telran.ilcarro.model.dto.user.RegistrationDto;
import com.telran.ilcarro.model.dto.user.UserBaseDto;
import com.telran.ilcarro.model.dto.user.UserDtoForUser;
import com.telran.ilcarro.service.interfaces.ListenerRemindPassword;
import com.telran.ilcarro.service.interfaces.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

@CrossOrigin
@RestController
@RequestMapping
@Slf4j
public class UserController {

    private final UserService userService;
    private final ListenerRemindPassword listener;

    @Autowired
    public UserController(UserService userService, ListenerRemindPassword listener) {
        this.userService = userService;
        this.listener = listener;
    }


    @ApiOperation(value = "Authorization user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDtoForUser.class),
            @ApiResponse(code = 400, message = "Error! Bad token"),
            @ApiResponse(code = 401, message = "Error! Unauthorized"),
            @ApiResponse(code = 404, message = "Error! Not found")
    })

    @GetMapping(value = "/user/login")
    public UserDtoForUser authUser(@RequestHeader("Authorization") String token) {
        try {
            return userService.authUser(token);
        } catch (RegistrationModelException | TokenValidationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            log.error("User with token :" + token + " - " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            log.error("User with token :" + token + " - " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @ApiOperation(value = "Registration new user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDtoForUser.class),
            @ApiResponse(code = 400, message = "Error! Bad email or password"),
            @ApiResponse(code = 403, message = "Error! Forbidden"),
            @ApiResponse(code = 409, message = "Error! User already exist")
    })

    @PostMapping(value = "/registration")
    public UserDtoForUser registrationUser(@RequestBody RegistrationDto registrationDto
            , @RequestHeader("Authorization") String token) {
        try {
            return userService.addUser(registrationDto, token);
        } catch (RegistrationModelException | TokenValidationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (AuthorizationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (ActionDeniedException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "Update user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!", response = UserDtoForUser.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! User unauthorized"),
            @ApiResponse(code = 404, message = "Error! User not found")
    })

    @PutMapping(value = "/user")
    public UserDtoForUser updateUser(@RequestHeader("Authorization") String token,
                                     @RequestHeader(value = "X-New-Password", required = false) String newPassword,
                                     @RequestBody UserBaseDto userBaseDto) {
        try {
            return userService.updateUser(newPassword, token, userBaseDto);
        } catch (RegistrationModelException | TokenValidationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (AuthorizationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @ApiOperation(value = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! User unauthorized"),
            @ApiResponse(code = 404, message = "Error! User not found"),
            @ApiResponse(code = 403, message = "Error! Forbidden")
    })

    @DeleteMapping(value = "/user")
    public void deleteUser(@RequestHeader("Authorization") String token) {
        try {
            userService.deleteUser(token);
        } catch (RegistrationModelException | TokenValidationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (AuthorizationException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (NotFoundException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ActionDeniedException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @ApiOperation(value = "Activate user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 404, message = "Error! User not found")
    })
    @PostMapping(value = "/activate/{code}")
    public String activate(@PathVariable String code) {
        try {
            return userService.activateUser(code);
        } catch (NotFoundException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @ApiOperation(value = "Remind password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 404, message = "Error! User not found")
    })
    @GetMapping(value = "/user/verify")
    public void remindPassword(HttpServletRequest request, @RequestHeader("Return-path") String email) {
        try {
            listener.getRemindPassword(email, request.getHeader("host"));
            userService.remindPassword(email);
        } catch (ActionDeniedException | NotFoundException ex) {
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @ApiOperation(value = "Get invoice")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 404, message = "Error! User not found")
    })
    @GetMapping(value = "/user/invoice")
    public void getInvoice(@RequestHeader("Authorization") String token
            , HttpServletResponse response, @RequestParam("order_id") String order_id) {
        try {
            userService.getInvoice(token, response, order_id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "Get booked list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK!"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 404, message = "Error! User not found")
    })
    @GetMapping(value = "/user/booked")
    public Iterable<BookedCarsDtoForUser> getHistory(@RequestHeader("Authorization") String token) {
        try {
            return userService.getHistory(token);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}