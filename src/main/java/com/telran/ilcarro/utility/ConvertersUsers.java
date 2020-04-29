package com.telran.ilcarro.utility;

import com.telran.ilcarro.model.Role;
import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.PersonWhoBookedDto;
import com.telran.ilcarro.model.dto.PickUpPlaceDto;
import com.telran.ilcarro.model.dto.StatisticsDto;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.model.dto.user.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConvertersUsers {

    private static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static UserDtoForUser createUserDtoFromUser(User user) {
        return UserDtoForUser.builder()
                .first_name(user.getFirstName())
                .second_name(user.getSecondName())
                .photo(user.getAvatar())
                .registration_date(user.getRegistrationDate().toString())
                .comments(createCommentDtoListFromCommentList(user.getComments()))
                .own_cars(createCarFullDtoListFromCarList(user.getOwnerCars()))
                .booked_cars(createBookedCarsDtoListFromBookedCarList(user.getBookedCars()))
                .history(createHistoryDtoListFromHistoryList(user.getHistory()))
                .build();
    }

    private static ArrayList<HistoryDtoForUser> createHistoryDtoListFromHistoryList(ArrayList<History> historyList) {
        ArrayList<HistoryDtoForUser> historyDtoForUserList = new ArrayList<>();
        if (!historyList.isEmpty()) {
            for (History history : historyList) {
                historyDtoForUserList.add(createHistoryDtoFromHistory(history));
            }
        }
        return historyDtoForUserList;
    }

    private static HistoryDtoForUser createHistoryDtoFromHistory(History history) {
        return HistoryDtoForUser.builder()
                .serial_number(history.getSerial_number())
                .booked_period(HistoryPeriodDto.builder()
                        .order_id(history.getHistory().getOrder_id())
                        .start_date_time(history.getHistory().getStart_date_time().format(formatterTime))
                        .end_date_time(history.getHistory().getEnd_date_time().format(formatterTime))
                        .paid(history.getHistory().isPaid())
                        .amount(history.getHistory().getAmount())
                        .booking_date(history.getHistory().getBooking_date())
                        .build())
                .build();
    }

    private static ArrayList<BookedCarsDtoForUser> createBookedCarsDtoListFromBookedCarList(ArrayList<BookedCars> bookedCarsList) {
        ArrayList<BookedCarsDtoForUser> bookedCarsDtoForUserList = new ArrayList<>();
        if (!bookedCarsList.isEmpty()) {
            for (BookedCars bookedCars : bookedCarsList) {
                bookedCarsDtoForUserList.add(createBookedCarsDtoFromBookedCars(bookedCars));
            }
        }
        return bookedCarsDtoForUserList;
    }

    private static BookedCarsDtoForUser createBookedCarsDtoFromBookedCars(BookedCars bookedCars) {
        return BookedCarsDtoForUser.builder()
                .serial_number(bookedCars.getSerial_number())
                .booked_period(createBookedPeriodDtoFromBookedPeriod(bookedCars.getBooked_period()))
                .build();
    }

    public static ArrayList<CommentDto> createCommentDtoListFromCommentList(List<Comment> commentList) {
        ArrayList<CommentDto> listComments = new ArrayList<>();
        if (!commentList.isEmpty()) {
            for (Comment comment : commentList) {
                listComments.add(createCommentDtoFromComment(comment));
            }
        }
        return listComments;
    }

    public static CommentDto createCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .first_name(comment.getFirst_name())
                .second_name(comment.getSecond_Name())
                .photo(comment.getPhoto())
                .post(comment.getContent())
                .post_date(comment.getPost_date().toString())
                .build();
    }

    public static ArrayList<OwnerCarDtoForUser> createCarFullDtoListFromCarList(ArrayList<Car> list) {
        ArrayList<OwnerCarDtoForUser> carFullDtoList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Car car : list) {
                carFullDtoList.add(createCarFullDtoFromCar(car));
            }
        }
        return carFullDtoList;
    }

    public static User createUserFromUserDto(UserDtoForUser userDtoForUser, String email, String password) {
        return User.builder()
                .email(email)
                .password(password)
                .firstName(userDtoForUser.getFirst_name())
                .secondName(userDtoForUser.getSecond_name())
                .avatar(userDtoForUser.getPhoto())
                .role(Role.ROLE_USER.name())
                .isActive(true)
                .activationCode(UUID.randomUUID().toString())
                .registrationDate(LocalDate.parse(userDtoForUser.getRegistration_date()
                        , DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .comments(new ArrayList<>())
                .ownerCars(new ArrayList<>())
                .bookedCars(new ArrayList<>())
                .history(new ArrayList<>())
                .build();
    }

    public static OwnerCarDtoForUser createCarFullDtoFromCar(Car car) {
        return OwnerCarDtoForUser.builder()
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
                .image_url(car.getImage_url())
                .booked_periods(createBookedPeriodsDtoListFromBookedPeriodList(car.getBooked_periods()))
                .statistics(createStatisticsDtoFromStatistics(car.getStatistics()))
                .build();
    }

    private static StatisticsDto createStatisticsDtoFromStatistics(Statistics statistics) {
        return StatisticsDto.builder().trips(String.valueOf(statistics.getTrips())).rating(String.valueOf(statistics.getRating()))
                .build();
    }

    private static ArrayList<BookedPeriodDto> createBookedPeriodsDtoListFromBookedPeriodList(ArrayList<BookedPeriod> booked_periods) {
        ArrayList<BookedPeriodDto> bookedPeriodDtoList = new ArrayList<>();
        if (!booked_periods.isEmpty()) {
            for (BookedPeriod bookedPeriod : booked_periods) {
                bookedPeriodDtoList.add(createBookedPeriodDtoFromBookedPeriod(bookedPeriod));
            }
        }
        return bookedPeriodDtoList;
    }

    private static BookedPeriodDto createBookedPeriodDtoFromBookedPeriod(BookedPeriod bookedPeriod) {
        String startDateTime = bookedPeriod.getStart_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getStart_date_time().toLocalTime().toString();
        String endDateTime = bookedPeriod.getEnd_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getEnd_date_time().toLocalTime().toString();
        return BookedPeriodDto.builder()
                .order_id(bookedPeriod.getOrder_id())
                .start_date_time(startDateTime)
                .end_date_time(endDateTime)
                .paid(bookedPeriod.isPaid())
                .amount(bookedPeriod.getAmount())
                .booking_date(bookedPeriod.getBooking_date())
                .person_who_booked(PersonWhoBookedDto.builder()
                        .email(bookedPeriod.getPerson_who_booked().getEmail())
                        .first_name(bookedPeriod.getPerson_who_booked().getFirst_name())
                        .second_name(bookedPeriod.getPerson_who_booked().getSecond_name())
                        .phone(bookedPeriod.getPerson_who_booked().getPhone())
                        .build())
                .build();
    }

    public static UserDtoForUser userDtoFromRegistrationDto(RegistrationDto registrationDto) {
        return UserDtoForUser.builder()
                .first_name(registrationDto.getFirst_name())
                .second_name(registrationDto.getSecond_name())
                .photo("http://res.cloudinary.com/dvde7hpxw/image/upload/v1586969454/jq7tc2f58djl3dk9hutx.jpg")
                .registration_date(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .comments(new ArrayList<>())
                .own_cars(new ArrayList<>())
                .booked_cars(new ArrayList<>())
                .history(new ArrayList<>()).build();
    }

    public static UserDtoForUser createUserDtoFromUserBaseDtoAndUser(UserBaseDto userBaseDto, User user) {
        return UserDtoForUser.builder()
                .first_name(userBaseDto.getFirst_name())
                .second_name(userBaseDto.getSecond_name())
                .photo(userBaseDto.getPhoto())
                .registration_date(user.getRegistrationDate().toString())
                .comments(createCommentDtoListFromCommentList(user.getComments()))
                .own_cars(createCarFullDtoListFromCarList(user.getOwnerCars()))
                .booked_cars(createBookedCarsDtoListFromBookedCarList(user.getBookedCars()))
                .history(createHistoryDtoListFromHistoryList(user.getHistory()))
                .build();
    }

    public static Owner updateOwnerFromUserBaseDtoAndUser(UserBaseDto userBaseDto, User user) {
        return Owner.builder()
                .second_name(userBaseDto.getSecond_name())
                .registration_date(user.getRegistrationDate())
                .first_name(userBaseDto.getFirst_name())
                .email(user.getEmail())
                .build();
    }

    public static User updateUserFromUserAndUserDtoForUserAndListCars(User user, String newPassword, UserDtoForUser newUserDtoForUser, List<Car> ownerCars) {
        return User.builder()
                .secondName(newUserDtoForUser.getSecond_name())
                .registrationDate(user.getRegistrationDate())
                .password(newPassword)
                .ownerCars(new ArrayList<>(ownerCars))
                .history(new ArrayList<>(user.getHistory()))
                .firstName(newUserDtoForUser.getFirst_name())
                .email(user.getEmail())
                .comments(new ArrayList<>(user.getComments()))
                .bookedCars(new ArrayList<>(user.getBookedCars()))
                .avatar(newUserDtoForUser.getPhoto())
                .activationCode(user.getActivationCode())
                .isActive(user.isActive())
                .role(user.getRole())
                .build();
    }

    public static User updateUserFromUserAndListCars(User user, ArrayList<Car> carsByOwner) {
        return User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .avatar(user.getAvatar())
                .registrationDate(user.getRegistrationDate())
                .comments(user.getComments())
                .ownerCars(carsByOwner)
                .bookedCars(user.getBookedCars())
                .history(user.getHistory())
                .role(user.getRole())
                .isActive(user.isActive())
                .activationCode(user.getActivationCode())
                .build();
    }

    public static Iterable<BookedCarsDtoForUser> createHistoryList(ArrayList<BookedCars> history) {
        return createBookedCarsDtoListFromBookedCarList(history);
    }
}