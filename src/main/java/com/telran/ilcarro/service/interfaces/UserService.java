package com.telran.ilcarro.service.interfaces;

import com.telran.ilcarro.model.dto.user.*;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    UserDtoForUser addUser(RegistrationDto userBaseDto, String token);

    UserDtoForUser updateUser(String newPassword, String token, UserBaseDto userBaseDto);

    void deleteUser(String token);

    UserDtoForUser authUser(String token);

    String activateUser(String code);

    void remindPassword(String email);

    void getInvoice(String token, HttpServletResponse response, String order_id) throws Exception;

    Iterable<BookedCarsDtoForUser> getHistory(String token);
}
