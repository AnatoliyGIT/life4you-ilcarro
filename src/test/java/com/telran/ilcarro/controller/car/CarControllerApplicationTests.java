package com.telran.ilcarro.controller.car;

import com.telran.ilcarro.controller.CarController;
import com.telran.ilcarro.controller.UserController;
import com.telran.ilcarro.model.documents.Car;
import com.telran.ilcarro.model.dto.PersonWhoBookedDto;
import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import com.telran.ilcarro.model.dto.ReservationDto;
import com.telran.ilcarro.model.dto.car.CarFullUploadRequestDto;
import com.telran.ilcarro.model.dto.user.RegistrationDto;
import com.telran.ilcarro.model.dto.user.UserDtoForUser;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.AccountCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CarControllerApplicationTests {

    private UserRepository userRepository;
    private AccountCredentials accountCredentials_1;
    private AccountCredentials accountCredentials_2;
    private String user_token_1;
    private String user_token_2;
    private String user_token_unauthorised_1;
    private String carNumber_1 = "10-200-10";
    private String carNumber_2 = "20-200-20";
    private UserController userController;
    private CarController carController;
    private CarRepository carRepository;
    private CarFullUploadRequestDto carFullUploadRequestDto_1;
    private CarFullUploadRequestDto carFullUploadRequestDto_2;
    private ReservationDto reservationDto_1;
    RegistrationDto regDto_1;
    RegistrationDto regDto_2;

    @Autowired
    CarControllerApplicationTests(UserController userController
            , CarController carController
            , CarRepository carRepository
            , UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.carController = carController;
        this.carRepository = carRepository;
        this.accountCredentials_1 = new AccountCredentials("test@mail.com", "Test2020");
        this.accountCredentials_2 = new AccountCredentials("test2@mail.com", "Test22020");
    }

    @Autowired
    public void setUp() {

        regDto_1 = RegistrationDto.builder()
                .first_name("test_First_Name_1")
                .second_name("test_Second_Name_1")
                .build();
        regDto_2 = RegistrationDto.builder()
                .first_name("test_First_Name_2")
                .second_name("test_Second_Name_2")
                .build();

        user_token_1 = Base64.getEncoder()
                .encodeToString((accountCredentials_1.email + ":" + accountCredentials_1.password).getBytes());
        user_token_2 = Base64.getEncoder()
                .encodeToString((accountCredentials_2.email + ":" + accountCredentials_2.password).getBytes());
        user_token_unauthorised_1 = Base64.getEncoder()
                .encodeToString((accountCredentials_1.email + ":" + "Test2021").getBytes());

        ArrayList<String> features = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        PickUpPlaceDto pickUpPlaceDto = PickUpPlaceDto.builder()
                .latitude(32.96).longitude(34.83).place_id("place_Id")
                .build();
        carFullUploadRequestDto_1 = new CarFullUploadRequestDto(carNumber_1, "Audi", "A4"
                , "2010", "Engine", "fuel", "gear", "wheels drive"
                , 100, 150, 4, 5, "C", 10
                , features, 150, 200, "About", pickUpPlaceDto, images);
        carFullUploadRequestDto_2 = new CarFullUploadRequestDto(carNumber_2, "Audi", "A8"
                , "2010", "Engine", "fuel", "gear", "wheels drive"
                , 100, 150, 4, 5, "A", 12
                , features, 150, 200, "About", pickUpPlaceDto, images);
        reservationDto_1 = ReservationDto.builder()
                .person_who_booked(PersonWhoBookedDto.builder()
                        .phone("1234567890")
                        .email("test_email@mail.com")
                        .first_name("test_first_name")
                        .second_name("test_second_name")
                        .build())
                .start_date_time("2020-02-10 08:00")
                .end_date_time("2020-02-10 20:00")
                .build();

    }

    @Test
    public void testAddCar() {
        userController.registrationUser(regDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
        assertNotNull(carRepository.findById(carNumber_1));
        assertEquals(carNumber_1, userRepository
                .findUserByEmail(accountCredentials_1.email).getOwnerCars().get(0).getSerial_number());
        userController.deleteUser(user_token_1);
    }

    @Test
    public void testAddCarResponsesErrorsBadRequest() {
        userController.registrationUser(regDto_1, user_token_1);
        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                carController.addCarByOwner(carFullUploadRequestDto_1, "qwerty"));
        assertEquals(badToken.getMessage().substring(0, 3), "400");
        userController.deleteUser(user_token_1);

    }

    @Test
    public void testAddCarResponsesErrorsUnauthorized() {
        userController.registrationUser(regDto_1, user_token_1);
        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                carController.addCarByOwner(carFullUploadRequestDto_1, user_token_unauthorised_1));
        assertEquals(badToken.getMessage().substring(0, 3), "401");
        userController.deleteUser(user_token_1);
    }

    @Test
    public void testAddCarResponsesErrorsAlreadyExist() {
        userController.registrationUser(regDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1));
        assertEquals(badToken.getMessage().substring(0, 3), "409");
        userController.deleteUser(user_token_1);
    }


    @Test
    public void testGetCarsByOwner() {
        userController.registrationUser(regDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_2, user_token_1);
        List<Car> cars = new ArrayList<>(carRepository.findCarsByOwnerEmail(accountCredentials_1.email));
        assertEquals(2, cars.size());
        assertNotNull(cars.stream().filter(car -> car.getSerial_number()
                .equals(carNumber_1)).findFirst().orElse(null));
        assertNotNull(cars.stream().filter(car -> car.getSerial_number()
                .equals(carNumber_2)).findFirst().orElse(null));
        userController.deleteUser(user_token_1);
    }

    @Test
    public void testGetCarById() {
        userController.registrationUser(regDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
        assertEquals(carNumber_1, carController.getCarByIdForUsers(carNumber_1).getSerial_number());
        userController.deleteUser(user_token_1);
    }

    @Test
    public void testGetCarByIdForOwner() {
        userController.registrationUser(regDto_1, user_token_1);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
        assertEquals(carNumber_1, carController.getOwnerCarById(carNumber_1, user_token_1).getSerial_number());
        userController.deleteUser(user_token_1);
    }

    @Test
    public void testMakeReservation() {
        userController.registrationUser(regDto_1, user_token_1);
        userController.registrationUser(regDto_2, user_token_2);
        carController.addCarByOwner(carFullUploadRequestDto_1, user_token_1);
//        carController.makeReservation(carNumber_1, reservationDto_1, user_token_2);
        UserDtoForUser userDtoForUser1 = userController.authUser(user_token_1);
        UserDtoForUser userDtoForUser2 = userController.authUser(user_token_2);
        assertEquals("2020-02-10 08:00", carController.getOwnerCarById(carNumber_1, user_token_1)
                .getBooked_periods().get(0).getStart_date_time());
        assertEquals("2020-02-10 20:00", carController.getOwnerCarById(carNumber_1, user_token_1)
                .getBooked_periods().get(0).getEnd_date_time());
        assertEquals("test_email@mail.com", carController.getOwnerCarById(carNumber_1, user_token_1)
                .getBooked_periods().get(0).getPerson_who_booked().getEmail());
        assertEquals(carNumber_1, userDtoForUser1.getHistory().get(0).getSerial_number());
        assertEquals(carNumber_1, userDtoForUser2.getBooked_cars().get(0).getSerial_number());
        userController.deleteUser(user_token_1);
        userController.deleteUser(user_token_2);
    }
}