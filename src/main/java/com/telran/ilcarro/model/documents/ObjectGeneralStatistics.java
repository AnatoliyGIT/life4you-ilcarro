package com.telran.ilcarro.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectGeneralStatistics {
    private int getCarByIdForUsers = 0;
    private int getThreeBestBookedCars = 0;
    private int getFilters = 0;
    private int getCarByDateLocationAndPrice = 0;
    private int getCarByLocation = 0;
    private int searchThatContainsAllSearches = 0;
    private int getCarByFilter = 0;
    private int getLatestComments = 0;
    private int updateTimerFilters = 0;
    private int downloadImages = 0;
    private int getLoggers = 0;
    private int activateUser = 0;
    private int remindPassword = 0;
}
