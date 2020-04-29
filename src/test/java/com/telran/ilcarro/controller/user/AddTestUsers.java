package com.telran.ilcarro.controller.user;

import com.telran.ilcarro.controller.CarController;
import com.telran.ilcarro.controller.UserController;
import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import com.telran.ilcarro.model.dto.car.CarFullUploadRequestDto;
import com.telran.ilcarro.model.dto.user.RegistrationDto;
import com.telran.ilcarro.service.AccountCredentials;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Base64;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AddTestUsers {

    private AccountCredentials accountCredentials_Vasya;
    private AccountCredentials accountCredentials_Johnny;
    private AccountCredentials accountCredentials_Sarah;
    private AccountCredentials accountCredentials_Vivi;
    private RegistrationDto regDto_vasya;
    private RegistrationDto regDto_johnny;
    private RegistrationDto regDto_sarah;
    private RegistrationDto regDto_vivi;
    private String user_token_vasya;
    private String user_token_johnny;
    private String user_token_sarah;
    private String user_token_vivi;
    private UserController userController;
    private CarController carController;
    CarFullUploadRequestDto audiDto;
    CarFullUploadRequestDto opelDto;
    CarFullUploadRequestDto volkswagenDto;

    @Autowired
    AddTestUsers(UserController userController
            , CarController carController) {
        this.userController = userController;
        this.carController = carController;
        this.accountCredentials_Vasya = new AccountCredentials("vasya@mail.com", "Vasya2019");
        this.accountCredentials_Johnny = new AccountCredentials("john.lennon@gmail.com", "John2019");
        this.accountCredentials_Sarah = new AccountCredentials("sara.connor@gmail.com", "Sara2019");
        this.accountCredentials_Vivi = new AccountCredentials("vivi.branch@gmail.com", "Vivi2019");
    }

    @Autowired
    public void setUp() {
        regDto_vasya = RegistrationDto.builder().first_name("VASYA").second_name("PUPKIN").build();
        regDto_johnny = RegistrationDto.builder().first_name("JOHNN").second_name("LENNON").build();
        regDto_sarah = RegistrationDto.builder().first_name("SARAH").second_name("CONNOR").build();
        regDto_vivi = RegistrationDto.builder().first_name("VIOLETTA").second_name("BRANCH").build();

        ArrayList<String> imagesAudi = new ArrayList<>();
        imagesAudi.add("https://upload.wikimedia.org/wikipedia/commons/0/0c/AudiD5.jpg");
        imagesAudi.add("https://upload.wikimedia.org/wikipedia/commons/0/0c/AudiD5.jpg");

        ArrayList<String> imagesOpel = new ArrayList<>();
        imagesOpel.add("https://upload.wikimedia.org/wikipedia/commons/c/c3/Opel_Astra_J_1.4_ecoFLEX_Edition_front_20100725.jpg");
        imagesOpel.add("https://upload.wikimedia.org/wikipedia/commons/c/c3/Opel_Astra_J_1.4_ecoFLEX_Edition_front_20100725.jpg");

        ArrayList<String> imagesVolkswagen = new ArrayList<>();
        imagesVolkswagen.add("https://upload.wikimedia.org/wikipedia/commons/0/04/2018_Volkswagen_Passat_GT_au_SIAM_2018.jpg");
        imagesVolkswagen.add("https://upload.wikimedia.org/wikipedia/commons/0/04/2018_Volkswagen_Passat_GT_au_SIAM_2018.jpg");

        ArrayList<String> features = new ArrayList<>();
        features.add("Feature-1");
        features.add("Feature-2");
        features.add("Feature-3");

        PickUpPlaceDto pick_up_place = PickUpPlaceDto.builder()
                .place_id("place-45456226").longitude(34.966039).latitude(32.829466).build();

        audiDto = new CarFullUploadRequestDto("11-111-11", "Audi", "A8", "2015"
                , "3.0L V6 DOHC 24V AWD", "Gas", "auto", "RWD", 150, 103
                , 4, 5, "C", 10, features, 150, 0
                , "========== AUDI A8, 3.0L-V6-DOHC-24V-AWD, 2015 ==========", pick_up_place, imagesAudi);
        opelDto = new CarFullUploadRequestDto("22-222-22", "Opel", "Astra", "2010"
                , "1.4 ecoFLEX", "Gas", "auto", "RWD", 120, 103
                , 5, 5, "C", 10, features, 130, 0
                , "========== OPEL ASTRA, 1.4-ecoFLEX, 2010 ==========", pick_up_place, imagesOpel);
        volkswagenDto = new CarFullUploadRequestDto("33-333-33", "Volkswagen", "Passat GT"
                , "2018"
                , "1.8 TSI", "Gas", "auto", "RWD", 150, 123
                , 4, 5, "C", 10, features, 180, 0
                , "========== VOLKSWAGEN PASSAT, 1.8-turbo, 2018 ==========", pick_up_place, imagesVolkswagen);

        user_token_vasya = Base64.getEncoder().encodeToString((accountCredentials_Vasya.email
                + ":" + accountCredentials_Vasya.password).getBytes());
        user_token_johnny = Base64.getEncoder().encodeToString((accountCredentials_Johnny.email
                + ":" + accountCredentials_Johnny.password).getBytes());
        user_token_sarah = Base64.getEncoder().encodeToString((accountCredentials_Sarah.email
                + ":" + accountCredentials_Sarah.password).getBytes());
        user_token_vivi = Base64.getEncoder().encodeToString((accountCredentials_Vivi.email
                + ":" + accountCredentials_Vivi.password).getBytes());
    }

    @Test
    public void addUsers() {
        userController.registrationUser(regDto_vasya, user_token_vasya);
        userController.registrationUser(regDto_johnny, user_token_johnny);
        userController.registrationUser(regDto_sarah, user_token_sarah);
        userController.registrationUser(regDto_vivi, user_token_vivi);
    }

    @Test
    public void addCarsToUsers() {
        carController.addCarByOwner(audiDto, user_token_vasya);
        carController.addCarByOwner(opelDto, user_token_vasya);
        carController.addCarByOwner(volkswagenDto, user_token_johnny);
    }

    @Test
    @Disabled
    public void deleteUsers() {
//        carRepository.deleteById("11-111-11");
//        carRepository.deleteById("22-222-22");
//        carRepository.deleteById("33-333-33");
//        carRepository.deleteById("44-444-44");
        userController.deleteUser(user_token_vasya);
        userController.deleteUser(user_token_johnny);
        userController.deleteUser(user_token_sarah);
        userController.deleteUser(user_token_vivi);
    }
}
