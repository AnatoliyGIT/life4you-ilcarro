package com.telran.ilcarro.model.dto.filters;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponseWithFilter {
    private String current_page;
    private String items_on_page;
    private String items_total;
    private Iterable<CarsFiltersDto> cars;
    private JsonNode filter;
}
