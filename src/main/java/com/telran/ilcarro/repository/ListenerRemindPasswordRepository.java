package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.ListenerRemind;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ListenerRemindPasswordRepository extends MongoRepository<ListenerRemind,String> {
}
