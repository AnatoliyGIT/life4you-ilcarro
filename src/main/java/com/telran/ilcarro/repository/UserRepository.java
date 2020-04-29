package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>, UserCustomRepository {
    User save(User user);
    User findUserByEmail(String email);
}
