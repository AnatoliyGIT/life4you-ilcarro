package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.Filter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilterRepository extends MongoRepository<Filter, String> {
}
