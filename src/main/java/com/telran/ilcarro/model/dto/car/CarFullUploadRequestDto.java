package com.telran.ilcarro.model.dto.car;

import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CarFullUploadRequestDto {
    @NonNull
    @NotEmpty
    @Size(min = 7, max = 12)
    private String serial_number;
    @NonNull
    @NotEmpty
    private String make;
    @NonNull
    @NotEmpty
    private String model;
    @NonNull
    @NotEmpty
    private String year;
    @NonNull
    @NotEmpty
    private String engine;
    @NonNull
    @NotEmpty
    private String fuel;
    @NonNull
    @NotEmpty
    private String gear;
    @NonNull
    @NotEmpty
    private String wheels_drive;
//    @NonNull
    @NotEmpty
    private int horsepower;
    private int torque;
    @NotEmpty
    @Min(value = 1, message = "Doors should not be less that 1")
    @Max(value = 10, message = "Doors should not be greater than 10")
    private int doors;
    @Min(value = 1, message = "Seats should not be less that 1")
    @Max(value = 20, message = "Seats should not be greater than 20")
    private int seats;
    @NonNull
    @NotEmpty
    @Size(min = 1, max = 20)
    private String car_class;
    @NotEmpty
    private float fuel_consumption;
    @NotEmpty
    private ArrayList<String> features;
    @NotEmpty
    @Min(value = 1, message = "Price should not be less that 1")
    private double price_per_day;
    @NotEmpty
    private double distance_included;
    @NonNull
    @NotEmpty
    @Size(min = 1, max = 500)
    private String about;
    @NonNull
    @NotEmpty
    private PickUpPlaceDto pick_up_place;
    @NonNull
    @NotEmpty
    private ArrayList<String> image_url;
}
