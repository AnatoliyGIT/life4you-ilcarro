package com.telran.ilcarro.repository;

import com.telran.ilcarro.model.documents.UsageStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageStatisticsRepository extends MongoRepository<UsageStatistics, String> {
    UsageStatistics save(UsageStatistics statistics);
    UsageStatistics findUsageStatisticsByEmail(String email);
}
