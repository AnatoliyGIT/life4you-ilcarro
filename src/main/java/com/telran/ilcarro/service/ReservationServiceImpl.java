package com.telran.ilcarro.service;

import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.ReservationDto;
import com.telran.ilcarro.model.dto.car.*;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.interfaces.ReservationService;
import com.telran.ilcarro.service.interfaces.TokenService;
import com.telran.ilcarro.utility.ConvertersCars;
import com.telran.ilcarro.utility.Reservation;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ReservationServiceImpl implements ReservationService {
    final HashMap<String, Timer> timerHashMap = new HashMap<>();
    final ConcurrentHashMap<String, Reservation> reservationHashMap = new ConcurrentHashMap<>();
    ReentrantLock locker = new ReentrantLock();
    private final CarRepository carRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public ReservationServiceImpl(CarRepository carRepository
            , TokenService tokenService
            , UserRepository userRepository) {
        this.carRepository = carRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public void cancelReservation(String serial_number, String start_date_time, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "reservationCancellation");
        if (Utils.isValidSerialNumberAuto(serial_number)) throw new CarDetailsException("No valid serial number");
        start_date_time = Utils.correctionDateAndTime(start_date_time);
        Utils.isValidFormatDateAndTime(start_date_time, start_date_time);
        LocalDateTime startDateTime = LocalDateTime
                .parse(start_date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Car car = carRepository.findById(serial_number).orElse(null);
        if (car == null)
            throw new NotFoundException("Car with serial_number : " + serial_number + " not found!");

        if (startDateTime.isBefore(LocalDateTime.now().plusHours(
                Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude()))))
            throw new ActionDeniedException("Cannot delete past bookings");

        User userCar = userRepository.findUserByEmail(accountCredentials.email);
        if (userCar == null || !userCar.isActive())
            throw new NotFoundException("User with email:" + accountCredentials.email + " not found!");
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        if (!userCar.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");

        User ownerCar = userRepository.findUserByEmail(car.getOwner().getEmail());
        if (ownerCar == null || !ownerCar.isActive())
            throw new NotFoundException("User with email:" + car.getOwner().getEmail() + " not found!");

        ArrayList<Car> cars_owner = ownerCar.getOwnerCars();
        ArrayList<BookedPeriod> periods = car.getBooked_periods();
        ArrayList<History> histories = ownerCar.getHistory();
        ArrayList<BookedCars> booked_cars = userCar.getBookedCars();

        BookedPeriod booked_period = periods.stream()
                .filter(period -> period.getStart_date_time().isEqual(startDateTime)).findFirst().orElse(null);
        if (booked_period == null) throw new NotFoundException("Booked period not found!!!");

        BookedCars booked = booked_cars.stream().filter(bookedCars -> bookedCars.getBooked_period()
                .getOrder_id().equals(booked_period.getOrder_id())).findFirst().orElse(null);
        if (booked == null)
            throw new ActionDeniedException("This user is not allowed to delete the reservation!!!");

        Car car_owner = cars_owner.stream().filter(car1 -> car1.getSerial_number()
                .equals(serial_number)).findFirst().orElse(null);
        if (car_owner == null) throw new NotFoundException("Car by owner not found!!!");
        cars_owner.remove(car_owner);
        History history = histories.stream().filter(his -> his.getSerial_number().equals(serial_number)
                && his.getHistory().getStart_date_time().isEqual(startDateTime)).findFirst().orElse(null);
        if (history == null) throw new NotFoundException("History for auto not found");
        histories.remove(history);
        ownerCar.setHistory(histories);
        periods.remove(booked_period);
        car_owner.setBooked_periods(periods);
        cars_owner.add(car_owner);
        car.setBooked_periods(periods);
        car.getStatistics().setTrips(car_owner.getStatistics().getTrips());
        ownerCar.setOwnerCars(cars_owner);
        BookedCars user_car_period = booked_cars.stream().filter(car_user -> car_user.getBooked_period().getStart_date_time()
                .equals(startDateTime)).findFirst().orElse(null);
        if (user_car_period == null) throw new NotFoundException("Booked car on user not fount");
        booked_cars.remove(user_car_period);
        userCar.setBookedCars(booked_cars);

        if (userCar.getEmail().equals(ownerCar.getEmail())) {
            userCar.setHistory(histories);
        }

        userRepository.save(ownerCar);
        userRepository.save(userCar);
        carRepository.save(car);

    }

    @Override
    public BookedPeriodBaseDto makeReservation(String serial_number
            , ReservationDto reservationDto, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "makeReservation");
        if (Utils.isValidSerialNumberAuto(serial_number))
            throw new CarDetailsException("No valid serial number");
        String startDate = reservationDto.getStart_date_time();
        String endDate = reservationDto.getEnd_date_time();
        reservationDto.setStart_date_time(Utils.correctionDateAndTime(startDate));
        reservationDto.setEnd_date_time(Utils.correctionDateAndTime(endDate));
        String start_date_time = reservationDto.getStart_date_time();
        String end_date_time = reservationDto.getEnd_date_time();
        Utils.isValidFormatDateAndTime(start_date_time, end_date_time);

        try {
            String first_name = reservationDto.getPerson_who_booked().getFirst_name();
            String second_name = reservationDto.getPerson_who_booked().getSecond_name();
            String email = reservationDto.getPerson_who_booked().getEmail();
            String phone = reservationDto.getPerson_who_booked().getPhone();
            if (email.isEmpty() || first_name.isEmpty()
                    || second_name.isEmpty() || phone.isEmpty()) {
                throw new RequestArgumentsException("All fields must be filled");
            }
        } catch (NullPointerException ex) {
            throw new RequestArgumentsException("ReservationDto can not be null or empty");
        }
        LocalDateTime startDateTime = LocalDateTime
                .parse(start_date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endDateTime = LocalDateTime
                .parse(end_date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (startDateTime.isEqual(endDateTime) || startDateTime.isAfter(endDateTime))
            throw new RequestArgumentsException("No valid dates. Dates cannot be equal," +
                    " or start_date_time cannot be later than end_date_time!");
        User userCar = userRepository.findUserByEmail(accountCredentials.email);
        if (userCar == null || !userCar.isActive())
            throw new NotFoundException("User with email:" + accountCredentials.email + " not found!");
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        if (!userCar.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        try {
            locker.lock();
            Car car = carRepository.findById(serial_number).orElse(null);
            if (car == null)
                throw new NotFoundException("Car with serial_number : " + serial_number + " not found!");
            if (startDateTime.plusMinutes(1).isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude())))
                    || endDateTime.plusMinutes(1).isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude()))))
                throw new IllegalArgumentException("Yesterday Date and Time!");
            boolean isAvailable = Utils.validationBookedPeriods(startDateTime, endDateTime, car);
            if (!isAvailable)
                throw new IllegalArgumentException("Busy Date");
            long hours = Utils.getHoursTotalBetweenTwoDates(startDateTime, endDateTime);
            double amount = (Math.ceil(car.getPrice_per_day() / 24 * hours));
            BookedPeriodForCarDto bookedPeriodForCarDto = ConvertersCars
                    .createNewBookedPeriodForCarDtoFromReservationDtoAndAmount(reservationDto, amount);
            BookedPeriod bookedPeriod = ConvertersCars
                    .createBookedPeriodFromBookedPeriodDto(bookedPeriodForCarDto);
            User owner = userRepository.findUserByEmail(car.getOwner().getEmail());
            Car carOwner = owner.getOwnerCars().stream()
                    .filter(carOwn -> carOwn.getSerial_number()
                            .equals(car.getSerial_number())).findFirst().orElseThrow(() -> new NotFoundException(""));
            ArrayList<BookedPeriod> bookedPeriodsOwner = carOwner.getBooked_periods();
            bookedPeriodsOwner.add(bookedPeriod);
            carOwner.setBooked_periods(bookedPeriodsOwner);
            owner.getOwnerCars().removeIf(c -> c.getSerial_number().equals(car.getSerial_number()));
            owner.getOwnerCars().add(carOwner);
            userRepository.save(owner);

            ArrayList<BookedPeriod> bookedPeriods = car.getBooked_periods();
            bookedPeriods.add(bookedPeriod);
            car.setBooked_periods(bookedPeriods);
            carRepository.save(car);
            bookingTimer(car.getSerial_number(), userCar.getEmail(), car.getOwner().getEmail(), bookedPeriod);

            return ConvertersCars.createBookedPeriodBaseDtoFromBookedPeriod(bookedPeriod);
        } finally {
            locker.unlock();
        }

    }

    @Override
    public void bookingTimer(String serialNumber, String userEmail, String ownerEmail, BookedPeriod bookedPeriod) {
        String bookedId = bookedPeriod.getOrder_id();
        if (bookedId == null) {
            throw new RequestArgumentsException("The field cannot be empty or null");
        }
        Timer timer = new Timer();
        timerHashMap.put(bookedId, timer);
        TimerTask task = new TimerTask() {
            int count = 1;

            @Override
            public void run() {
                if (count == 300) {
                    Car car = carRepository.findById(serialNumber).orElseThrow(() -> new NotFoundException(""));
                    User owner = userRepository.findUserByEmail(car.getOwner().getEmail());
                    ArrayList<BookedPeriod> bookedPeriods = car.getBooked_periods();
                    bookedPeriods.removeIf(period -> period.getOrder_id().equals(bookedId));
                    car.setBooked_periods(bookedPeriods);
                    carRepository.save(car);
                    Car carOwner = owner.getOwnerCars().stream().filter(c -> c.getSerial_number()
                            .equals(car.getSerial_number())).findFirst().orElseThrow(() -> new NotFoundException(""));
                    carOwner.getBooked_periods().removeIf(bookedPeriod -> bookedPeriod
                            .getOrder_id().equals(bookedId));
                    owner.getOwnerCars().removeIf(c -> c.getSerial_number()
                            .equals(car.getSerial_number()));
                    owner.getOwnerCars().add(carOwner);
                    userRepository.save(owner);
                    timer.cancel();
                    timerHashMap.remove(bookedId);
                    reservationHashMap.remove(bookedId);

                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                            + " Reservation canceled: Car number: " + car.getSerial_number());
                }
                count++;
            }
        };
        timer.schedule(task, 1000, 1000);
        Reservation reservation = new Reservation(serialNumber, ownerEmail, userEmail, bookedPeriod);
        reservationHashMap.put(bookedId, reservation);

        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                + " Reservation " + reservationHashMap.keySet().size()
                + " -> Car number: " + reservation.getSerialNumber());

    }

    @Override
    public void bookingPayment(String token, String bookedId) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "paymentForReservation");
        Timer timer = timerHashMap.get(timerHashMap.keySet().stream()
                .filter(key -> key.equals(bookedId)).findFirst().orElse(null));
        if (timer == null)
            throw new NotFoundException("Booking period not found");
        Reservation reservation = reservationHashMap.get(bookedId);
        if (reservation == null)
            throw new NotFoundException("Booked period not found");
        User owner = userRepository.findUserByEmail(reservation.getOwnerEmail());
        User user = userRepository.findUserByEmail(reservation.getUserEmail());
        String password = accountCredentials.password;
        String email = accountCredentials.email;
        if (!user.getPassword().equals(tokenService.encodePassword(password)))
            throw new AuthorizationException("User unauthorized");
        if (!email.equals(user.getEmail()))
            throw new ActionDeniedException("User banned to pay");

        locker.lock();
        try {
            Car carNew = carRepository.findById(reservation.getSerialNumber()).orElse(null);
            if (carNew == null) throw new NotFoundException("Car not fount in repository");

            System.out.print(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + " Car: " + reservation.getSerialNumber());
            System.out.print(", booked period: " + reservation.getBookedPeriod().getStart_date_time()
                    .toLocalDate() + " " + reservation.getBookedPeriod().getStart_date_time().toLocalTime()
                    + " ... " + reservation.getBookedPeriod().getEnd_date_time().toLocalDate() + " "
                    + reservation.getBookedPeriod().getEnd_date_time().toLocalTime());

            carNew.getBooked_periods().removeIf(bookedPeriod -> !bookedPeriod.isPaid()
                    && bookedPeriod.getOrder_id().equals(bookedId));
            boolean isAvailable = Utils.validationBookedPeriods(
                    reservation.getBookedPeriod().getStart_date_time()
                    , reservation.getBookedPeriod().getEnd_date_time(), carNew);
            if (!isAvailable) {
                System.out.println(" - Reservation not paid (already taken)");
                throw new IllegalArgumentException("Date and time are already taken");
            }
            ArrayList<BookedPeriod> bookedPeriodList = carNew.getBooked_periods();
            reservation.getBookedPeriod().setPaid(true);
            bookedPeriodList.add(reservation.getBookedPeriod());
            carNew.setBooked_periods(bookedPeriodList);
            owner.getOwnerCars()
                    .removeIf(c -> c.getSerial_number().equals(carNew.getSerial_number()));
            owner.getOwnerCars().add(carNew);
            HistoryCars historyCars = ConvertersCars.createHistoryCarsFromBookedPeriodAndAmount(
                    reservation.getBookedPeriod()
                    , reservation.getBookedPeriod().getAmount());
            History history = ConvertersCars.createHistoryFromHistoryCars(
                    carNew.getSerial_number(), historyCars);
            BookedCars bookedCars = ConvertersCars.createBookedCarsFromBookedPeriod(
                    carNew.getSerial_number(), reservation.getBookedPeriod());
            ArrayList<History> histories = owner.getHistory();
            histories.add(history);
            owner.setHistory(histories);
            ArrayList<BookedCars> bookedCarsList = user.getBookedCars();
            bookedCarsList.add(bookedCars);
            user.setBookedCars(bookedCarsList);
            if (owner.getEmail().equals(user.getEmail()))
                owner.setBookedCars(bookedCarsList);
            carRepository.save(carNew);
            userRepository.save(user);
            userRepository.save(owner);
        } finally {
            locker.unlock();
        }
        System.out.println(" - Car booking paid");
        timer.cancel();
        timerHashMap.remove(bookedId);
        reservationHashMap.remove(bookedId);
    }

    @Override
    public OwnerCarDtoForCar reserveCarByIdForOwner(String serial_number
            , ArrayList<ReservedPeriodDto> dto, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "lockCarForBookingCarId");
        dto = Utils.correctionDateAndTime(dto);
        User user = userRepository.findUserByEmail(accountCredentials.email);
        Car car = carRepository.findById(serial_number).orElse(null);
        if(car == null) throw new NotFoundException("Car not found");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (Utils.isValidUserAndCar(accountCredentials, encodePassword, user, car)) {
            throw new TokenValidationException("User unauthorized or User does not own a car");
        }
        ArrayList<ReservedPeriod> reservedPeriodList = new ArrayList<>();
        for (ReservedPeriodDto periodDto : dto) {
            ReservedPeriod period = ConvertersCars.createReservedPeriodFromReservedPeriodDto(periodDto);
            if (period.getStart_date_time().isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude())))
                    || period.getEnd_date_time().isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude()))))
                throw new IllegalArgumentException("Yesterday Date and Time!");
            if (period.getStart_date_time().isEqual(period.getEnd_date_time())
                    || period.getStart_date_time().isAfter(period.getEnd_date_time())) {
                throw new IllegalArgumentException("dates cannot be equal or the start date" +
                        " cannot be after the end date");
            }
            reservedPeriodList.add(period);
        }
        boolean isAvailable = Utils.validationReservedPeriods(reservedPeriodList, car);
        if (!isAvailable)
            throw new IllegalArgumentException("Dates busy");
        ArrayList<ReservedPeriod> reservedPeriods = car.getReserved_periods();
        reservedPeriods.addAll(reservedPeriodList);
        car.setReserved_periods(reservedPeriods);

        ArrayList<Car> ownerCars = new ArrayList<>();
        for (Car c : user.getOwnerCars()) {
            if (c.getSerial_number().equals(car.getSerial_number())) {
                c.setReserved_periods(reservedPeriods);
            }
            ownerCars.add(c);
        }
        user.setOwnerCars(ownerCars);

        carRepository.save(car);
        userRepository.save(user);
        return ConvertersCars.createOwnerCarDtoFromCarFullDto(ConvertersCars.createCarFullDtoFromCar(car));
    }

    @Override
    public OwnerCarDtoForCar freeCarByIdForOwner(String serial_number
            , ArrayList<ReservedPeriodDto> dto, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "unlockCarForBookingCarId");
        dto = Utils.correctionDateAndTime(dto);
        User user = userRepository.findUserByEmail(accountCredentials.email);
        Car car = carRepository.findById(serial_number).orElse(null);
        if (car == null) throw new NotFoundException("Car not found");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (Utils.isValidUserAndCar(accountCredentials, encodePassword, user, car)) {
            throw new TokenValidationException("User unauthorized or User does not own a car");
        }
        ArrayList<ReservedPeriod> reservedPeriodRequestList = new ArrayList<>();
        for (ReservedPeriodDto periodDto : dto) {
            ReservedPeriod period = ConvertersCars.createReservedPeriodFromReservedPeriodDto(periodDto);
            if (period.getStart_date_time().isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude())))
                    || period.getEnd_date_time().isBefore(LocalDateTime.now().plusHours(
                    Utils.correctionTimeZone(car.getPick_up_place().getGeolocation().getLongitude()))))
                throw new IllegalArgumentException("Yesterday Date and Time!");
            reservedPeriodRequestList.add(period);
            if (period.getStart_date_time().isEqual(period.getEnd_date_time())
                    || period.getStart_date_time().isAfter(period.getEnd_date_time())) {
                throw new IllegalArgumentException("dates cannot be equal or the start date" +
                        " cannot be after the end date");
            }
        }
        ArrayList<ReservedPeriod> reservedPeriodCarList = car.getReserved_periods();
        if (reservedPeriodCarList.isEmpty())
            throw new NotFoundException("Reserved periods not founds");
        int count = 0;
        for (int i = 0; i < reservedPeriodCarList.size(); i++) {
            if (reservedPeriodCarList.size() < reservedPeriodRequestList.size()) {
                throw new RequestArgumentsException("More periods than can be removed");
            }
            for (ReservedPeriod reservedPeriod : reservedPeriodRequestList) {
                if (reservedPeriodCarList.get(i).getStart_date_time()
                        .equals(reservedPeriod.getStart_date_time())
                        && reservedPeriodCarList.get(i).getEnd_date_time()
                        .equals(reservedPeriod.getEnd_date_time())) {
                    reservedPeriodCarList.remove(reservedPeriodCarList.get(i));
                    count++;
                }
            }
        }
        if (reservedPeriodRequestList.size() != count)
            throw new RequestArgumentsException("Periods in car and request do not match");
        car.setReserved_periods(reservedPeriodCarList);
        user.getOwnerCars().removeIf(c -> c.getSerial_number().equals(car.getSerial_number()));
        user.getOwnerCars().add(car);
        carRepository.save(car);
        userRepository.save(user);
        return ConvertersCars.createOwnerCarDtoFromCarFullDto(ConvertersCars.createCarFullDtoFromCar(car));
    }
}
