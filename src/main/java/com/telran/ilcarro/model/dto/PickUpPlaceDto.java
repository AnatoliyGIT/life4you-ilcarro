package com.telran.ilcarro.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class PickUpPlaceDto {
    private String place_id;
    private double latitude;
    private double longitude;
}
