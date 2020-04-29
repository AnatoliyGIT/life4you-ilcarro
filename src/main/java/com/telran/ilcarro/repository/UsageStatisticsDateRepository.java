package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.UsageStatisticsYesterday;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageStatisticsDateRepository extends MongoRepository<UsageStatisticsYesterday, String> {
    UsageStatisticsYesterday findByDate(String date);
    UsageStatisticsYesterday save(UsageStatisticsYesterday usageStatisticsYesterday);
}
