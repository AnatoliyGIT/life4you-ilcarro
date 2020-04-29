package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class UserCustomRepositoryImpl implements UserCustomRepository {
    private MongoTemplate mongoTemplate;

    @Autowired
    public UserCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> findUserByActivationCode(String code) {
        Query query = new Query();
        query.addCriteria(Criteria.where("activationCode").is(code));
        return mongoTemplate.find(query, User.class);
    }
}
