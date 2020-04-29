package com.telran.ilcarro.utility;

import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import com.telran.ilcarro.model.dto.StatisticsDto;
import com.telran.ilcarro.model.dto.car.ReservedPeriodDto;
import com.telran.ilcarro.model.dto.filters.BookedPeriodDateDto;
import com.telran.ilcarro.model.dto.filters.CarsFiltersDto;
import com.telran.ilcarro.model.dto.filters.CommentDtoForFilters;
import com.telran.ilcarro.model.dto.filters.OwnerDto;

import java.util.ArrayList;
import java.util.List;

public final class ConvertersFilters {

    private static StatisticsDto createStatisticsDtoFromStatistics(Statistics statistics) {
        return StatisticsDto.builder().trips(String.valueOf(statistics.getTrips())).rating(String.valueOf(statistics.getRating()))
                .build();
    }

    public static CarsFiltersDto createCarForUsersDtoFromCar(Car car) {
        ArrayList<String> imgUrl = Utils.imageUrlDefault(car.getImage_url());
        return CarsFiltersDto.builder()
                .serial_number(car.getSerial_number())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .engine(car.getEngine())
                .fuel(car.getFuel())
                .gear(car.getGear())
                .wheels_drive(car.getWheels_drive())
                .horsepower(car.getHorse_power())
                .torque(car.getTorque())
                .doors(car.getDoors())
                .seats(car.getSeats())
                .car_class(car.getCar_class())
                .fuel_consumption(car.getFuel_consumption())
                .features(car.getFeatures())
                .price_per_day(car.getPrice_per_day())
                .distance_included(car.getDistance_included())
                .about(car.getAbout())
                .pick_up_place(PickUpPlaceDto.builder().
                        place_id(car.getPick_up_place().getPlace_id())
                        .latitude(car.getPick_up_place().getGeolocation().getLatitude())
                        .longitude(car.getPick_up_place().getGeolocation().getLongitude()).build())
                .image_url(imgUrl)
                .owner(OwnerDto.builder()
                        //.email(car.getOwner().getEmail())
                        .first_name(car.getOwner().getFirst_name())
                        .second_name(car.getOwner().getSecond_name())
                        .registration_date(car.getOwner().getRegistration_date().toString())
                        .build())
                .booked_periods(createBookedPeriodsDateDtoListFromBookedPeriodList(car.getBooked_periods()))
                .statistics(createStatisticsDtoFromStatistics(car.getStatistics()))
                .comments(createCommentsDtoForCars(car.getComments()))
                .reserved_periods(createReservedPeriodDtoListFromReservedPeriodList(
                        car.getReserved_periods()))
                .build();

    }

    private static ArrayList<ReservedPeriodDto> createReservedPeriodDtoListFromReservedPeriodList(
            ArrayList<ReservedPeriod> reserved_periods) {
        ArrayList<ReservedPeriodDto> reservedPeriodDtoArrayList = new ArrayList<>();
        for (ReservedPeriod reservedPeriod:reserved_periods) {
            reservedPeriodDtoArrayList.add(createReservedPeriodDtoFromReservedPeriod(
                    reservedPeriod));
        }
        return reservedPeriodDtoArrayList;
    }

    private static ReservedPeriodDto createReservedPeriodDtoFromReservedPeriod(ReservedPeriod reservedPeriod) {
        return ReservedPeriodDto.builder()
                .end_date_time(reservedPeriod.getEnd_date_time().toLocalDate().toString() + " "
                        + reservedPeriod.getEnd_date_time().toLocalTime().toString())
                .start_date_time(reservedPeriod.getStart_date_time().toLocalDate().toString() + " "
                        + reservedPeriod.getStart_date_time().toLocalTime().toString())
                .build();
    }

    private static Iterable<CommentDtoForFilters> createCommentsDtoForCars(ArrayList<Comment> comments) {
        ArrayList<CommentDtoForFilters> list = new ArrayList<>();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                list.add(CommentDtoForFilters.builder()
                        .first_name(comment.getFirst_name())
                        .second_name(comment.getSecond_Name())
                        .photo(comment.getPhoto())
                        .post(comment.getContent())
                        .post_date(comment.getPost_date().toString())
                        .build());
            }
        }
        return list;
    }

    private static ArrayList<BookedPeriodDateDto> createBookedPeriodsDateDtoListFromBookedPeriodList(ArrayList<BookedPeriod> booked_periods) {
        ArrayList<BookedPeriodDateDto> list = new ArrayList<>();
        if (!booked_periods.isEmpty()) {
            for (BookedPeriod bookedPeriod : booked_periods) {
                list.add(createBookedPeriodDateFromBookedPeriod(bookedPeriod));
            }
        }
        return list;
    }

    private static BookedPeriodDateDto createBookedPeriodDateFromBookedPeriod(BookedPeriod bookedPeriod) {
        return BookedPeriodDateDto.builder()
                .start_date_time(bookedPeriod.getStart_date_time().toLocalDate().toString()
                        + " " + bookedPeriod.getStart_date_time().toLocalTime().toString())
                .end_date_time(bookedPeriod.getEnd_date_time().toLocalDate().toString()
                        + " " + bookedPeriod.getEnd_date_time().toLocalTime().toString())
                .build();
    }

    public static Iterable<CarsFiltersDto> createCarForUsersDtoListFromCarList(List<Car> cars) {
        List<CarsFiltersDto> list = new ArrayList<>();
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                list.add(createCarForUsersDtoFromCar(car));
            }
        }
        return list;
    }
}
