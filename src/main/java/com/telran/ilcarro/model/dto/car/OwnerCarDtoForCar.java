package com.telran.ilcarro.model.dto.car;

import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import com.telran.ilcarro.model.dto.StatisticsDto;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

@Data
@Builder
public class OwnerCarDtoForCar {
    @NonNull
    @NotEmpty
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
    private String gear;
    @NonNull
    @NotEmpty
    private String wheels_drive;
//    @NonNull
    @NotEmpty
    private int horsepower;
    private int torque;
//    @NonNull
    @NotEmpty
    private int doors;
//    @NonNull
    @NotEmpty
    private int seats;
    @NonNull
    @NotEmpty
    private String car_class;
//    @NonNull
    @NotEmpty
    private float fuel_consumption;
    @NonNull
    @NotEmpty
    private ArrayList<String> features;
    @NonNull
    @NotEmpty
    private PriceDto price_per_day;
//    @NonNull
    @NotEmpty
    private double distance_included;
    @NonNull
    @NotEmpty
    private String about;
    @NonNull
    @NotEmpty
    private PickUpPlaceDto pick_up_place;
    @NonNull
    @NotEmpty
    private ArrayList<String> image_url;
    @NonNull
    @NotEmpty
    private ArrayList<BookedPeriodForCarDto> booked_periods;
    @NonNull
    private ArrayList<ReservedPeriodDto> reserved_periods;
    @NonNull
    @NotEmpty
    private StatisticsDto statistics;
    @NonNull
    private ArrayList<CommentDto> comments;
}
