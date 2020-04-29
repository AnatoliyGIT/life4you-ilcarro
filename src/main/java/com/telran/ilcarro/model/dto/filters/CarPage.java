package com.telran.ilcarro.model.dto.filters;

import com.telran.ilcarro.model.documents.Car;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class CarPage {
    int currentPage;
    int itemsOnPage;
    int totalItems;
    List<Car> cars;
}
