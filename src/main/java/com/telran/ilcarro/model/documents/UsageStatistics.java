package com.telran.ilcarro.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "UsageStatisticsToday")
public class UsageStatistics {
    private String time;
    @Id
    private String email;
    private ObjectUserStatistics objectUserStatistics;
    private ObjectGeneralStatistics objectGeneralStatistics;
}
