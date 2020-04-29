package com.telran.ilcarro.service;

import com.telran.ilcarro.exception.RegistrationModelException;
import com.telran.ilcarro.exception.TokenValidationException;
import com.telran.ilcarro.service.interfaces.TokenService;
import com.telran.ilcarro.utility.Utils;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenServiceImpl implements TokenService {

    @Override
    public AccountCredentials decodeToken(String token) {
        try{
        int index = token.indexOf(" ");
        token = token.substring(index + 1);
            Base64.getDecoder().decode(token);
        }catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException ex) {
            throw new TokenValidationException("Bad token");
        }
        byte[] base64DecodeBytes = Base64.getDecoder().decode(token);
        token = new String(base64DecodeBytes);
        String[] auth = token.split(":");
        if (auth.length > 2) throw new TokenValidationException("Length token no valid");
        if (Utils.isValidEmail(auth[0])) {
            throw new RegistrationModelException("No valid Email!");
        }
        if (Utils.isValidPassword(auth[1])) {
            throw new RegistrationModelException("No valid password");
        }
        return new AccountCredentials(auth[0].toLowerCase(), auth[1]);
    }

    @Override
    public String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    @Override
    public String decodePassword(String password) {
        byte[] base64DecodeBytes = Base64.getDecoder().decode(password);
        password = new String(base64DecodeBytes);
        return password;
    }
}

