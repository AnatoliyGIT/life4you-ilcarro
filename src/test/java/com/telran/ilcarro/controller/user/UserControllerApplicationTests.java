package com.telran.ilcarro.controller.user;

import com.telran.ilcarro.controller.UserController;
import com.telran.ilcarro.model.documents.User;
import com.telran.ilcarro.model.dto.user.RegistrationDto;
import com.telran.ilcarro.model.dto.user.UserBaseDto;
import com.telran.ilcarro.model.dto.user.UserDtoForUser;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.AccountCredentials;
import com.telran.ilcarro.service.interfaces.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserControllerApplicationTests {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserController userController;
    @Autowired
    TokenService tokenService;
    AccountCredentials accountCredentials_1 = new AccountCredentials("test@mail.com", "Test2020");
    RegistrationDto regDto_1;
    String user_token_1;
    String unauthorized_user_token_1;
    String user_not_found_token;
    User user;

    @AfterEach
    public void deleteTestUser() {
        if (userRepository.findUserByEmail(accountCredentials_1.email) != null) {
            userRepository.delete(userRepository.findUserByEmail(accountCredentials_1.email));
        }
    }

    @Autowired
    public void setUp() {
        if (userRepository.findUserByEmail(accountCredentials_1.email) != null) {
            userRepository.delete(userRepository.findUserByEmail(accountCredentials_1.email));
        }
        regDto_1 = RegistrationDto.builder()
                .first_name("test_First_Name_1")
                .second_name("test_Second_Name_1")
                .build();

        user_token_1 = Base64.getEncoder()
                .encodeToString((accountCredentials_1.email + ":" + accountCredentials_1.password).getBytes());
        unauthorized_user_token_1 = Base64.getEncoder()
                .encodeToString((accountCredentials_1.email + ":" + "Test2021").getBytes());
        user_not_found_token = Base64.getEncoder()
                .encodeToString(("testnotfound@mail.com" + ":" + "TestNotFound2020").getBytes());

        user = User.builder()
                .avatar("/src/image")
                .bookedCars(new ArrayList<>())
                .comments(new ArrayList<>())
                .email("test@mail.com")
                .firstName("test_First_Name_1")
                .history(new ArrayList<>())
                .ownerCars(new ArrayList<>())
                .password(tokenService.encodePassword("Test2020"))
                .registrationDate(LocalDate.now())
                .secondName("test_Second_Name_1")
                .build();
    }

    @Test
    public void testAddUser() {
        userController.registrationUser(regDto_1, user_token_1);
        User userRepo = userRepository.findUserByEmail("test@mail.com");
        assertEquals(userRepo.toString(), user.toString());
    }

    @Test
    void testAddUserBadRequest() {
        Throwable badFirstName = assertThrows(ResponseStatusException.class, () ->
                userController.registrationUser(RegistrationDto.builder()
                        .first_name("").second_name("111").build(), user_token_1));
        assertEquals(badFirstName.getMessage().substring(0, 3), "400");

        Throwable badSecondName = assertThrows(ResponseStatusException.class, () ->
                userController.registrationUser(RegistrationDto.builder()
                        .first_name("111").second_name("").build(), user_token_1));
        assertEquals(badSecondName.getMessage().substring(0, 3), "400");

        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                userController.registrationUser(RegistrationDto.builder()
                        .first_name("111").second_name("111").build(), ""));
        assertEquals(badToken.getMessage().substring(0, 3), "400");
    }

    @Test
    void testAddUserAlreadyExist() {
        userController.registrationUser(regDto_1, user_token_1);
        Throwable alreadyExist = assertThrows(ResponseStatusException.class, () ->
                userController.registrationUser(regDto_1, user_token_1));
        assertEquals(alreadyExist.getMessage().substring(0, 3), "409");
    }

    @Test
    public void testDeleteUser() {
        userController.registrationUser(regDto_1, user_token_1);
        userController.deleteUser(user_token_1);
        User userRepoAfter = userRepository.findUserByEmail("test@mail.com");
        assertNull(userRepoAfter);
    }

    @Test
    void testDeleteUserBadRequest() {
        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                userController.deleteUser("qwerty"));
        assertEquals(badToken.getMessage().substring(0, 3), "400");
    }

    @Test
    void testDeleteUserNotFound() {
        Throwable notFound = assertThrows(ResponseStatusException.class, () ->
                userController.deleteUser(user_not_found_token));
        assertEquals(notFound.getMessage().substring(0, 3), "404");
    }

    @Test
    void testDeleteUserUnauthorized() {
        userController.registrationUser(regDto_1, user_token_1);
        Throwable unauthorized = assertThrows(ResponseStatusException.class, () ->
                userController.deleteUser(unauthorized_user_token_1));
        assertEquals(unauthorized.getMessage().substring(0, 3), "401");
    }

    @Test
    public void testUpdateUser() {
        UserDtoForUser registrationUser = userController.registrationUser(regDto_1, user_token_1);
        String new_password = "newTest2020";
        String new_user_token = Base64.getEncoder()
                .encodeToString((accountCredentials_1.email + ":" + new_password).getBytes());
        userController.updateUser(user_token_1, tokenService.encodePassword(new_password), UserBaseDto.builder()
                .first_name("test_First_Name_update")
                .photo("photo_update")
                .second_name("test_Second_Name_update")
                .build());
        User userRepo = userRepository.findUserByEmail("test@mail.com");
        assertNotEquals(registrationUser.getFirst_name(), "test_First_Name_update");
        assertEquals(userRepo.getFirstName(), "test_First_Name_update");
        assertEquals(userRepo.getSecondName(), "test_Second_Name_update");
        assertEquals(userRepo.getAvatar(), "photo_update");
        assertEquals(userController.authUser(new_user_token).getFirst_name(), "test_First_Name_update");
    }

    @Test
    void testUpdateUserBadRequest() {
        userController.registrationUser(regDto_1, user_token_1);
        String new_password = "newTest2020";
        Throwable badPhoto = assertThrows(ResponseStatusException.class, () ->
                userController.updateUser("bad_token", tokenService.encodePassword(new_password)
                        , UserBaseDto.builder().build()));
        assertEquals(badPhoto.getMessage().substring(0, 3), "400");
    }

    @Test
    void testUpdateUserNotFound() {
        userController.registrationUser(regDto_1, user_token_1);
        String new_password = "newTest2020";
        Throwable notFound = assertThrows(ResponseStatusException.class, () ->
                userController.updateUser(user_not_found_token, new_password
                        , UserBaseDto.builder().second_name("secondNameUpdate")
                                .first_name("firstNameUpdate").photo("newPhoto").build()));
        assertEquals(notFound.getMessage().substring(0, 3), "404");
    }

    @Test
    void testUpdateUserUnauthorized() {
        userController.registrationUser(regDto_1, user_token_1);
        String new_password = "newTest2020";
        Throwable unauthorized = assertThrows(ResponseStatusException.class, () ->
                userController.updateUser(unauthorized_user_token_1
                        , new_password, UserBaseDto.builder().second_name("secondNameUpdate")
                                .first_name("firstNameUpdate").photo("newPhoto").build()));
        assertEquals(unauthorized.getMessage().substring(0, 3), "401");
    }

    @Test
    public void testAuthUser() {
        userController.registrationUser(regDto_1, user_token_1);
        UserDtoForUser userDtoForUser = userController.authUser(user_token_1);
        assertEquals(userDtoForUser.getFirst_name(), regDto_1.getFirst_name());
        assertEquals(userDtoForUser.getSecond_name(), regDto_1.getSecond_name());
    }

    @Test
    void testAuthUserBadRequest() {
        Throwable badToken = assertThrows(ResponseStatusException.class, () ->
                userController.authUser("qwerty"));
        assertEquals(badToken.getMessage().substring(0, 3), "400");
    }

    @Test
    void testAuthUserNotFound() {
        Throwable notFound = assertThrows(ResponseStatusException.class, () ->
                userController.authUser(user_token_1));
        assertEquals(notFound.getMessage().substring(0, 3), "404");
    }

    @Test
    void testAuthUserUnauthorized() {
        userController.registrationUser(regDto_1, user_token_1);
        Throwable unauthorized = assertThrows(ResponseStatusException.class, () ->
                userController.authUser(unauthorized_user_token_1));
        assertEquals(unauthorized.getMessage().substring(0, 3), "401");
    }
}
