package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> findUserByActivationCode(String code);
}
