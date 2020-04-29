package com.telran.ilcarro.service.interfaces;

import com.telran.ilcarro.service.AccountCredentials;

public interface TokenService {
    AccountCredentials decodeToken(String token);

    String encodePassword(String password);

    String decodePassword(String password);
}
