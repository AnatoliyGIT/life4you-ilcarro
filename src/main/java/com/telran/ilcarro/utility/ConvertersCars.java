package com.telran.ilcarro.utility;

import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.*;
import com.telran.ilcarro.model.dto.car.*;
import com.telran.ilcarro.model.dto.comment.CommentDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class ConvertersCars {
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static CarFullDto createCarFullDtoFromRequest(CarFullUploadRequestDto dto, UserDtoForCar userDtoForCar) {
        OwnerDtoForCar ownerDtoForCar = OwnerDtoForCar.builder()
                .first_name(userDtoForCar.getFirst_name())
                .second_name(userDtoForCar.getSecond_name())
                .registration_date(userDtoForCar.getRegistration_date())
                .build();
        PickUpPlaceDto pickUpPlaceDto = PickUpPlaceDto.builder().
                place_id(dto.getPick_up_place().getPlace_id())
                .latitude(dto.getPick_up_place().getLatitude())
                .longitude(dto.getPick_up_place().getLongitude()).build();
        StatisticsDto statisticsDto = StatisticsDto.builder().trips(String.valueOf(0)).rating(String.valueOf(0)).build();
        return CarFullDto.builder()
                .serial_number(dto.getSerial_number())
                .make(dto.getMake())
                .model(dto.getModel())
                .year(dto.getYear())
                .engine(dto.getEngine())
                .fuel(dto.getFuel())
                .gear(dto.getGear())
                .wheels_drive(dto.getWheels_drive())
                .horsepower(dto.getHorsepower())
                .torque(dto.getTorque())
                .doors(dto.getDoors())
                .seats(dto.getSeats())
                .car_class(dto.getCar_class())
                .fuel_consumption(dto.getFuel_consumption())
                .features(dto.getFeatures())
                .price_per_day(dto.getPrice_per_day())
                .distance_included(dto.getDistance_included())
                .about(dto.getAbout())
                .pick_up_place(pickUpPlaceDto)
                .image_url(dto.getImage_url())
                .owner(ownerDtoForCar)
                .booked_periods(new ArrayList<>())
                .reserved_periods(new ArrayList<>())
                .statistics(statisticsDto)
                .comments(new ArrayList<>())
                .build();
    }

    public static UserDtoForCar createUserDtoFromUser(User user) {
        return UserDtoForCar.builder()
                .first_name(user.getFirstName())
                .second_name(user.getSecondName())
                .photo(user.getAvatar())
                .registration_date(user.getRegistrationDate().toString())
                .comments(createCommentDtoListFromCommentList(user.getComments()))
                .owner_cars(createCarFullDtoListFromCarList(user.getOwnerCars()))
                .booked_cars(createBookedCarsDtoListFromBookedCarList(user.getBookedCars()))
                .history(createHistoryDtoListFromHistoryList(user.getHistory()))
                .build();
    }

    private static ArrayList<HistoryDtoForCar> createHistoryDtoListFromHistoryList(ArrayList<History> historyList) {
        ArrayList<HistoryDtoForCar> historyDtoForCarList = new ArrayList<>();
        if (!historyList.isEmpty()) {
            for (History history : historyList) {
                historyDtoForCarList.add(createHistoryDtoFromHistory(history));
            }
        }
        return historyDtoForCarList;
    }

    private static HistoryDtoForCar createHistoryDtoFromHistory(History history) {
        return HistoryDtoForCar.builder()
                .serial_number(history.getSerial_number())
                .booked_period(HistoryPeriodDtoForCar.builder()
                        .order_id(history.getHistory().getOrder_id())
                        .start_date_time(history.getHistory().getStart_date_time().toString())
                        .end_date_time(history.getHistory().getEnd_date_time().toString())
                        .amount(history.getHistory().getAmount())
                        .paid(history.getHistory().isPaid())
                        .booking_date(history.getHistory().getBooking_date())
                        .build())
                .build();
    }

    private static ArrayList<BookedCarsDtoForCar> createBookedCarsDtoListFromBookedCarList(ArrayList<BookedCars> bookedCarsList) {
        ArrayList<BookedCarsDtoForCar> bookedCarsDtoForCarList = new ArrayList<>();
        if (!bookedCarsList.isEmpty()) {
            for (BookedCars bookedCars : bookedCarsList) {
                bookedCarsDtoForCarList.add(createBookedCarsDtoFromBookedCars(bookedCars));
            }
        }
        return bookedCarsDtoForCarList;
    }

    private static BookedCarsDtoForCar createBookedCarsDtoFromBookedCars(BookedCars bookedCars) {
        return BookedCarsDtoForCar.builder()
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

    public static ArrayList<CarFullDto> createCarFullDtoListFromCarList(List<Car> list) {
        ArrayList<CarFullDto> carFullDtoList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Car car : list) {
                carFullDtoList.add(createCarFullDtoFromCar(car));
            }
        }
        return carFullDtoList;
    }

    public static CarFullDto createCarFullDtoFromCar(Car car) {
        ArrayList<String> imgUrl = Utils.imageUrlDefault(car.getImage_url());
        PickUpPlaceDto pickUpPlaceDto = PickUpPlaceDto.builder().
                place_id(car.getPick_up_place().getPlace_id())
                .latitude(car.getPick_up_place().getGeolocation().getLatitude())
                .longitude(car.getPick_up_place().getGeolocation().getLongitude()).build();
        OwnerDtoForCar ownerDtoForCar = OwnerDtoForCar.builder()
                .first_name(car.getOwner().getFirst_name())
                .second_name(car.getOwner().getSecond_name())
                .registration_date(car.getOwner().getRegistration_date().toString())
                .build();
        return CarFullDto.builder()
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
                .pick_up_place(pickUpPlaceDto)
                .image_url(imgUrl)
                .owner(ownerDtoForCar)
                .booked_periods(createBookedPeriodsDtoListFromBookedPeriodList(car.getBooked_periods()))
                .statistics(createStatisticsDtoFromStatistics(car.getStatistics()))
                .reserved_periods(ConvertersCars.createReservedPeriodDtoListFromReservedPeriodList(
                        car.getReserved_periods()))
                .comments(ConvertersComments.createCommentDtoListFromCommentList(car.getComments()))
                .build();
    }

    private static StatisticsDto createStatisticsDtoFromStatistics(Statistics statistics) {
        return StatisticsDto.builder().trips(String.valueOf(statistics.getTrips())).rating(String.valueOf(statistics.getRating()))
                .build();
    }

    private static ArrayList<BookedPeriodForCarDto> createBookedPeriodsDtoListFromBookedPeriodList(ArrayList<BookedPeriod> booked_periods) {
        ArrayList<BookedPeriodForCarDto> bookedPeriodForCarDtoList = new ArrayList<>();
        if (!booked_periods.isEmpty()) {
            for (BookedPeriod bookedPeriod : booked_periods) {
                bookedPeriodForCarDtoList.add(createBookedPeriodDtoFromBookedPeriod(bookedPeriod));
            }
        }
        return bookedPeriodForCarDtoList;
    }

    private static BookedPeriodForCarDto createBookedPeriodDtoFromBookedPeriod(BookedPeriod bookedPeriod) {
        String startDateTime = bookedPeriod.getStart_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getStart_date_time().toLocalTime().toString();
        String endDateTime = bookedPeriod.getEnd_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getEnd_date_time().toLocalTime().toString();
        return BookedPeriodForCarDto.builder()
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

    public static Car createCarFromCarFullDto(CarFullDto car, String email) {
        ArrayList<Comment> comments = new ArrayList<>();
        PickUpPlace pickUpPlace = PickUpPlace.builder().
                place_id(car.getPick_up_place().getPlace_id())
                .geolocation(Location.builder().latitude(car.getPick_up_place().getLatitude())
                        .longitude(car.getPick_up_place().getLongitude()).build())
                .build();
        Owner owner = Owner.builder()
                .email(email)
                .first_name(car.getOwner().getFirst_name())
                .second_name(car.getOwner().getSecond_name())
                .registration_date(LocalDate.parse(car.getOwner().getRegistration_date(), formatterDate))
                .build();
        return Car.builder()
                .serial_number(car.getSerial_number())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .engine(car.getEngine())
                .fuel(car.getFuel())
                .gear(car.getGear())
                .wheels_drive(car.getWheels_drive())
                .horse_power(car.getHorsepower())
                .torque(car.getTorque())
                .doors(car.getDoors())
                .seats(car.getSeats())
                .car_class(car.getCar_class())
                .fuel_consumption(car.getFuel_consumption())
                .features(car.getFeatures())
                .price_per_day(car.getPrice_per_day())
                .distance_included(car.getDistance_included())
                .about(car.getAbout())
                .pick_up_place(pickUpPlace)
                .image_url(car.getImage_url())
                .owner(owner)
                .booked_periods(createBookedPeriodsListFromBookedPeriodDtoList(car.getBooked_periods()))
                .statistics(createStatisticsFromStatisticsDto(car.getStatistics()))
                .comments(comments)
                .reserved_periods(ConvertersCars.createReservedPeriodListFromReservedPeriodDtoList(
                        car.getReserved_periods()))
                .build();
    }

    private static Statistics createStatisticsFromStatisticsDto(StatisticsDto statistics) {
        return Statistics.builder().trips(Integer.valueOf(statistics.getTrips())).rating(Integer.valueOf(statistics.getRating())).build();
    }

    private static ArrayList<BookedPeriod> createBookedPeriodsListFromBookedPeriodDtoList(ArrayList<BookedPeriodForCarDto> booked_periods) {
        ArrayList<BookedPeriod> bookedPeriods = new ArrayList<>();
        if (!booked_periods.isEmpty()) {
            for (BookedPeriodForCarDto bookedPeriodForCarDto : booked_periods)
                bookedPeriods.add(createBookedPeriodFromBookedDto(bookedPeriodForCarDto));
        }
        return bookedPeriods;
    }

    private static BookedPeriod createBookedPeriodFromBookedDto(BookedPeriodForCarDto bookedPeriodForCarDto) {
        return BookedPeriod.builder()
                .order_id(bookedPeriodForCarDto.getOrder_id())
                .start_date_time(LocalDateTime.parse(bookedPeriodForCarDto.getStart_date_time(), formatterTime))
                .end_date_time(LocalDateTime.parse(bookedPeriodForCarDto.getEnd_date_time(), formatterTime))
                .paid(bookedPeriodForCarDto.isPaid())
                .amount(bookedPeriodForCarDto.getAmount())
                .booking_date(bookedPeriodForCarDto.getBooking_date())
                .build();
    }


    public static BookedPeriod createBookedPeriodFromBookedPeriodDto(BookedPeriodForCarDto bookedPeriodForCarDto) {
        LocalDateTime start = LocalDateTime.parse(bookedPeriodForCarDto.getStart_date_time(), formatterTime);
        start.atZone(ZoneId.of("Asia/Jerusalem"));
        LocalDateTime end = LocalDateTime.parse(bookedPeriodForCarDto.getEnd_date_time(), formatterTime);
        end.atZone(ZoneId.of("Asia/Jerusalem"));
        return BookedPeriod.builder()
                .order_id(bookedPeriodForCarDto.getOrder_id())
                .start_date_time(start)
                .end_date_time(end)
                .paid(bookedPeriodForCarDto.isPaid())
                .amount(bookedPeriodForCarDto.getAmount())
                .booking_date(bookedPeriodForCarDto.getBooking_date())
                .person_who_booked(PersonWhoBooked.builder()
                        .email(bookedPeriodForCarDto.getPerson_who_booked().getEmail())
                        .first_name(bookedPeriodForCarDto.getPerson_who_booked().getFirst_name())
                        .second_name(bookedPeriodForCarDto.getPerson_who_booked().getSecond_name())
                        .phone(bookedPeriodForCarDto.getPerson_who_booked().getPhone())
                        .build())
                .build();
    }

    public static ArrayList<BookedPeriodForCarDto> createBookedPeriodDtoListFromBookedList(ArrayList<BookedPeriod> booked_periods) {
        ArrayList<BookedPeriodForCarDto> list = new ArrayList<>();
        if (!booked_periods.isEmpty()) {
            for (BookedPeriod bookedPeriod : booked_periods) {
                list.add(createBookedPeriodDtoFromBookedPeriod(bookedPeriod));
            }
        }
        return list;
    }

    public static PickUpPlace createCarPicUpPlaceFromCarDtoPlace(PickUpPlaceDto pick_up_place) {
        return PickUpPlace.builder()
                .place_id(pick_up_place.getPlace_id())
                .geolocation(Location.builder().latitude(pick_up_place.getLatitude())
                        .longitude(pick_up_place.getLongitude())
                        .build())
                .build();
    }

    public static OwnerCarDtoForCar createOwnerCarDtoFromCarFullDto(CarFullDto car) {
        ArrayList<String> imgUrl = Utils.imageUrlDefault(car.getImage_url());
        PickUpPlaceDto pickUpPlaceDto = PickUpPlaceDto.builder().
                place_id(car.getPick_up_place().getPlace_id())
                .latitude(car.getPick_up_place().getLatitude())
                .longitude(car.getPick_up_place().getLongitude()).build();
        return OwnerCarDtoForCar.builder()
                .serial_number(car.getSerial_number())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .engine(car.getEngine())
                .fuel(car.getFuel())
                .gear(car.getGear())
                .wheels_drive(car.getWheels_drive())
                .horsepower(car.getHorsepower())
                .torque(car.getTorque())
                .doors(car.getDoors())
                .seats(car.getSeats())
                .car_class(car.getCar_class())
                .fuel_consumption(car.getFuel_consumption())
                .features(car.getFeatures())
                .price_per_day(PriceDto.builder()
                        .currency(Currency.ILS.name())
                        .amount(car.getPrice_per_day())
                        .build())
                .distance_included(car.getDistance_included())
                .about(car.getAbout())
                .pick_up_place(pickUpPlaceDto)
                .image_url(imgUrl)
                .booked_periods(car.getBooked_periods())
                .statistics(car.getStatistics())
                .reserved_periods(car.getReserved_periods())
                .comments(car.getComments())
                .build();
    }

    public static Iterable<OwnerCarDtoForCar> createOwnerCarListFromCarFullList(ArrayList<CarFullDto> listCarFull) {
        ArrayList<OwnerCarDtoForCar> list = new ArrayList<>();
        if (!listCarFull.isEmpty()) {
            for (CarFullDto carFullDto : listCarFull) {
                list.add(createOwnerCarDtoFromCarFullDto(carFullDto));
            }
        }
        return list;
    }

    public static CarForUsersDto createCarForUsersDtoFromCar(Car car, ArrayList<CommentDto> comments) {
        ArrayList<String> imgUrl = Utils.imageUrlDefault(car.getImage_url());
        PickUpPlaceDto pickUpPlaceDto = PickUpPlaceDto.builder().
                place_id(car.getPick_up_place().getPlace_id())
                .latitude(car.getPick_up_place().getGeolocation().getLatitude())
                .longitude(car.getPick_up_place().getGeolocation().getLongitude()).build();
        OwnerDtoForCar ownerDtoForCar = OwnerDtoForCar.builder()
                .first_name(car.getOwner().getFirst_name())
                .second_name(car.getOwner().getSecond_name())
                .registration_date(car.getOwner().getRegistration_date().toString())
                .build();
        return CarForUsersDto.builder()
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
                .pick_up_place(pickUpPlaceDto)
                .image_url(imgUrl)
                .owner(ownerDtoForCar)
                .booked_periods(createBookedPeriodsDateDtoListFromBookedPeriodList(car.getBooked_periods()))
                .statistics(createStatisticsDtoFromStatistics(car.getStatistics()))
                .comments(comments)
                .reserved_periods(createReservedPeriodDtoListFromReservedPeriodList(car.getReserved_periods()))
                .build();

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
        String startDateTime = bookedPeriod.getStart_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getStart_date_time().toLocalTime().toString();
        String endDateTime = bookedPeriod.getEnd_date_time().toLocalDate().toString()
                + " " + bookedPeriod.getEnd_date_time().toLocalTime().toString();
        return BookedPeriodDateDto.builder()
                .start_date_time(startDateTime)
                .end_date_time(endDateTime)
                .build();
    }

    public static Iterable<CarForUsersDto> createCarForUsersDtoListFromCarList(List<Car> cars
            , HashMap<String, ArrayList<CommentDto>> mapComments) {
        ArrayList<CarForUsersDto> list = new ArrayList<>();
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                ArrayList<CommentDto> comments = mapComments.get(car.getSerial_number());
                list.add(createCarForUsersDtoFromCar(car, comments));
            }
        }
        return list;
    }

    public static BookedPeriodForCarDto createNewBookedPeriodForCarDtoFromReservationDtoAndAmount(ReservationDto reservationDto, double amount) {
        return BookedPeriodForCarDto.builder()
                .order_id(UUID.randomUUID().toString())
                .start_date_time(reservationDto.getStart_date_time())
                .end_date_time(reservationDto.getEnd_date_time())
                .paid(false)
                .amount(amount)
                .booking_date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .person_who_booked(reservationDto.getPerson_who_booked())
                .build();
    }

    public static BookedPeriodBaseDto createBookedPeriodBaseDtoFromBookedPeriod(BookedPeriod bookedPeriod) {
        return BookedPeriodBaseDto.builder()
                .amount(bookedPeriod.getAmount())
                .booking_date(bookedPeriod.getBooking_date())
                .order_number(bookedPeriod.getOrder_id())
                .build();
    }

    public static HistoryCars createHistoryCarsFromBookedPeriodAndAmount(BookedPeriod bookedPeriod, double amount) {
        return HistoryCars.builder()
                .amount(amount)
                .booking_date(bookedPeriod.getBooking_date())
                .end_date_time(bookedPeriod.getEnd_date_time())
                .start_date_time(bookedPeriod.getStart_date_time())
                .order_id(bookedPeriod.getOrder_id())
                .paid(bookedPeriod.isPaid())
                .person_who_booked(bookedPeriod.getPerson_who_booked())
                .build();
    }

    public static ReservedPeriod createReservedPeriodFromReservedPeriodDto(ReservedPeriodDto periodDto) {
        LocalDateTime startDateTime = LocalDateTime
                .parse(periodDto.getStart_date_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endDateTime = LocalDateTime
                .parse(periodDto.getEnd_date_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return ReservedPeriod.builder()
                .start_date_time(startDateTime)
                .end_date_time(endDateTime)
                .build();
    }

    public static ReservedPeriodDto createReservedPeriodDtoFromReservedPeriod(ReservedPeriod period) {
        return ReservedPeriodDto.builder()
                .start_date_time(period.getStart_date_time().toLocalDate().toString() + " "
                        + period.getStart_date_time().toLocalTime().toString())
                .end_date_time(period.getEnd_date_time().toLocalDate().toString() + " "
                        + period.getEnd_date_time().toLocalTime().toString())
                .build();
    }

    public static ArrayList<ReservedPeriodDto> createReservedPeriodDtoListFromReservedPeriodList(
            ArrayList<ReservedPeriod> periods) {
        ArrayList<ReservedPeriodDto> reservedPeriodDtoArrayList = new ArrayList<>();
        for (ReservedPeriod reservedPeriod : periods) {
            reservedPeriodDtoArrayList.add(createReservedPeriodDtoFromReservedPeriod(reservedPeriod));
        }
        return reservedPeriodDtoArrayList;
    }

    public static ArrayList<ReservedPeriod> createReservedPeriodListFromReservedPeriodDtoList(
            ArrayList<ReservedPeriodDto> periods) {
        ArrayList<ReservedPeriod> reservedPeriodArrayList = new ArrayList<>();
        for (ReservedPeriodDto reservedPeriodDto : periods) {
            reservedPeriodArrayList.add(createReservedPeriodFromReservedPeriodDto(reservedPeriodDto));
        }
        return reservedPeriodArrayList;
    }

    public static History createHistoryFromHistoryCars(String serialNumber, HistoryCars historyCars) {
        return History.builder()
                .serial_number(serialNumber)
                .history(historyCars)
                .build();
    }

    public static BookedCars createBookedCarsFromBookedPeriod(String serial_number, BookedPeriod bookedPeriod) {
        return BookedCars.builder()
                .serial_number(serial_number)
                .booked_period(bookedPeriod)
                .build();
    }

    public static CarLastOrderDto createCarLastOrderDtoFromCarAndBookedPeriod(Car car, BookedPeriod bookedPeriod) {
        return CarLastOrderDto.builder()
                .booked_periods(ConvertersCars.createBookedPeriodDateFromBookedPeriod(bookedPeriod))
                .person_who_booked(ConvertersCars
                        .createBookedPeriodDtoFromBookedPeriod(bookedPeriod).getPerson_who_booked())
                .car(ConvertersCars.createCarFullDtoFromCar(car))
                .build();
    }
}