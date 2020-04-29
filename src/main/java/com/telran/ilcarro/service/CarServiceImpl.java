package com.telran.ilcarro.service;

import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.documents.BookedCars;
import com.telran.ilcarro.model.documents.BookedPeriod;
import com.telran.ilcarro.model.documents.Car;
import com.telran.ilcarro.model.documents.User;
import com.telran.ilcarro.model.dto.car.*;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.interfaces.CarService;
import com.telran.ilcarro.service.interfaces.TokenService;
import com.telran.ilcarro.utility.ConvertersCars;
import com.telran.ilcarro.utility.ConvertersComments;
import com.telran.ilcarro.utility.ConvertersUsers;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository
            , TokenService tokenService
            , UserRepository userRepository) {
        this.carRepository = carRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    /*
     * @author Dmitry Asmalouski
     * @throws NotFoundException
     *  if Car with serial_number not found
     */
    @Override
    public OwnerCarDtoForCar getOwnerCarById(String serial_number, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "ownerGetCarBySerialNumber");
        updateTrips();
        User user = userRepository.findUserByEmail(accountCredentials.email);
        if ((user == null || !user.isActive())) throw new AuthorizationException("User with email:"
                + accountCredentials.email + " not found!");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        UserDtoForCar userDtoForCar = ConvertersCars.createUserDtoFromUser(user);
        ArrayList<CarFullDto> list = userDtoForCar.getOwner_cars();
        list.stream().filter(car -> car.getSerial_number().equals(serial_number))
                .findFirst().orElseThrow(() -> new NotFoundException("Car with serial_number : " + serial_number
                + " not found by user"));
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new NotFoundException("Car not found in car repository"));
        updateUserByComment(car);
        return ConvertersCars.createOwnerCarDtoFromCarFullDto(ConvertersCars.createCarFullDtoFromCar(car));
    }

    //@author Anatoly
    @Override
    public CarFullDto addCarByOwner(String token, CarFullUploadRequestDto carDto) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "addNewCar");
        if (carDto.getDistance_included() <= 0D || carDto.getHorsepower() <= 0D)
            throw new RequestArgumentsException("Horse power or distance included less or equals zero!");
        if (carDto.getPick_up_place() == null)
            throw new RequestArgumentsException("Bad request (pick up place = null)");
        if (carDto.getPick_up_place().getLatitude() > 80 || carDto.getPick_up_place().getLatitude() < -80)
            throw new CarDetailsException("Latitude field should be in the range from -80 to 80");
        if (carDto.getPick_up_place().getLongitude() > 180 || carDto.getPick_up_place().getLongitude() < -180)
            throw new CarDetailsException("Longitude field should be in the range from -180 to 180");
        String serial_number = carDto.getSerial_number();
        if (Utils.isValidSerialNumberAuto(serial_number)) throw new CarDetailsException("No valid serial number");
        if (carRepository.findById(serial_number).isPresent())
            throw new CarAlreadyExistException("Car with serial number: " + serial_number + " already exist");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        User user = userRepository.findUserByEmail(email);
        if (user == null || !user.isActive()) {
            throw new NotFoundException("User with email: " + email + " not found");
        }
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("User unauthorized, Wrong password!");
        try {
            UserDtoForCar userDtoForCar = ConvertersCars.createUserDtoFromUser(user);
            CarFullDto carFullDto = ConvertersCars.createCarFullDtoFromRequest(carDto, userDtoForCar);
            Car car = ConvertersCars.createCarFromCarFullDto(carFullDto, email);
            if (carDto.getPick_up_place().getLongitude() == 0 && carDto.getPick_up_place().getLatitude() == 0) {
                ifLocationZero(car);
            }
            ArrayList<Car> carsByOwner = user.getOwnerCars();
            carsByOwner.add(car);
            user = ConvertersUsers.updateUserFromUserAndListCars(user, carsByOwner);
            userRepository.save(user);
            carRepository.save(car);
            return carFullDto;
        } catch (NullPointerException ex) {
            throw new CarDetailsException("One or more input fields are not filled");
        } catch (Exception ex) {
            throw new CarDetailsException("JSON Parse exception");
        }
    }


    //@author Dmitry
    @Override
    public Iterable<OwnerCarDtoForCar> getOwnerCars(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "ownerGetCars");
        updateTrips();
        User user = userRepository.findUserByEmail(accountCredentials.email);
        if (user == null || !user.isActive()) throw new NotFoundException("User with email: "
                + accountCredentials.email + " not found!");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        ArrayList<CarFullDto> list = ConvertersCars.createCarFullDtoListFromCarList(user.getOwnerCars());
        List<String> numbers = new ArrayList<>();
        list.forEach(car -> numbers.add(car.getSerial_number()));
        List<Car> carList = carRepository.getCarsByNumberList(numbers);
        ArrayList<CarFullDto> carsFullDto = new ArrayList<>();
        if (!numbers.isEmpty()) {
            for (Car car : carList) {
                if (car != null) {
                    updateUserByComment(car);
                    carsFullDto.add(ConvertersCars.createCarFullDtoFromCar(car));
                }
            }
            return ConvertersCars.createOwnerCarListFromCarFullList(carsFullDto);
        }
        throw new NotFoundException("Cars not found!");
    }

    //@Anatoly
    @Override
    public CarFullDto updateCar(String serial_number, CarFullUploadRequestDto dto, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "updateCar");
        if (dto.getPick_up_place() == null || dto.getPick_up_place().getPlace_id() == null)
            throw new RequestArgumentsException("Bad request ('pick up place' or 'place id' = null)");
        if (Utils.isValidSerialNumberAuto(serial_number))
            throw new CarDetailsException("No valid serial number in params");
        String serialNumberFromRequest = dto.getSerial_number();
        if (Utils.isValidSerialNumberAuto(serialNumberFromRequest))
            throw new CarDetailsException("No valid serial number in request");
        if (dto.getPick_up_place().getLatitude() > 80 || dto.getPick_up_place().getLatitude() < -80)
            throw new CarDetailsException("Latitude field should be in the range from -80 to 80");
        if (dto.getPick_up_place().getLongitude() > 180 || dto.getPick_up_place().getLongitude() < -180)
            throw new CarDetailsException("Longitude field should be in the range from -180 to 180");
        User user = userRepository.findUserByEmail(accountCredentials.email);
        if (user == null || !user.isActive())
            throw new NotFoundException("User with email: " + accountCredentials.email + " not found!");
        String userEncodePassword = user.getPassword();
        String tokenEncodePassword = tokenService.encodePassword(accountCredentials.password);
        if (!userEncodePassword.equals(tokenEncodePassword))
            throw new AuthorizationException("User with email: " + accountCredentials.email +
                    " not authorized");
        Car carFromRepository = carRepository.findById(serial_number).orElse(null);
        if (carFromRepository == null) throw new NotFoundException("Car with serial number " + serial_number
                + " not found");
        ArrayList<Car> carsFromUser = user.getOwnerCars();
        Car carFromUser = carsFromUser.stream().filter(carBuf -> carBuf.getSerial_number().equals(serial_number))
                .findFirst().orElse(null);
        if (carFromUser == null) throw new CarAlreadyExistException("Car number "
                + serial_number + " does not belong to the owner");
        Car car = carRepository.findById(dto.getSerial_number()).orElse(null);
        if (car != null && !car.getSerial_number().equals(dto.getSerial_number()))
            throw new CarAlreadyExistException("Cannot set serial number (auto with serial number: "
                    + dto.getSerial_number() + " already exists in the repository)");
        CarFullDto carFullDto = ConvertersCars.createCarFullDtoFromRequest(dto
                , ConvertersCars.createUserDtoFromUser(user));
        Car newCar = ConvertersCars.createCarFromCarFullDto(carFullDto, user.getEmail());
        newCar.setBooked_periods(carFromUser.getBooked_periods());
        newCar.setOwner(carFromUser.getOwner());
        newCar.setPick_up_place(ConvertersCars.createCarPicUpPlaceFromCarDtoPlace(dto.getPick_up_place()));
        newCar.setStatistics(carFromUser.getStatistics());
        newCar.setBooked_periods(carFromRepository.getBooked_periods());
        newCar.setComments(carFromRepository.getComments());
        newCar.setReserved_periods(carFromRepository.getReserved_periods());
        if (dto.getPick_up_place().getLongitude() == 0 && dto.getPick_up_place().getLatitude() == 0) {
            ifLocationZero(newCar);
        }
        user.getOwnerCars().remove(carFromUser);
        user.getOwnerCars().add(newCar);
        userRepository.save(user);
        carRepository.deleteById(carFromRepository.getSerial_number());
        carRepository.save(newCar);
        return ConvertersCars.createCarFullDtoFromCar(newCar);
    }

    //@author Dmitry
    //@Anatoly
    @Override
    public void deleteCarBySerialNumber(String serial_number, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "deleteCar");
        if (Utils.isValidSerialNumberAuto(serial_number)) throw new CarDetailsException("No valid serial number");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        User user = userRepository.findUserByEmail(email);
        if (user == null || !user.isActive()) throw new NotFoundException("User not found!");
        String encodePassword = tokenService.encodePassword(password);
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        ArrayList<Car> list = user.getOwnerCars();
        Car car = list.stream().filter(carBuf -> carBuf.getSerial_number().equals(serial_number))
                .findFirst().orElseThrow(() -> new NotFoundException("Car serial number : " + serial_number
                        + " not found in " + accountCredentials.email));
        for (Car c : list) {
            for (BookedPeriod bookedPeriod : c.getBooked_periods()) {
                if (bookedPeriod.getEnd_date_time()
                        .isAfter(LocalDateTime.now().plusHours(Utils.correctionTimeZone(
                                c.getPick_up_place().getGeolocation().getLongitude())))) {
                    throw new ActionDeniedException(
                            "The car cannot be deleted because it has an active reservation");
                }
            }
        }
        user.getOwnerCars().remove(car);
        carRepository.deleteById(serial_number);
        userRepository.save(user);
    }

    @Override
    public Iterable<BookedPeriodForCarDto> getBookedPeriodsCarById(String serial_number, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "ownerGetBookedPeriodByCarId");
        if (Utils.isValidSerialNumberAuto(serial_number)) throw new CarDetailsException("No valid serial number");
        User user = userRepository.findUserByEmail(accountCredentials.email);
        String password = accountCredentials.password;
        if (user == null || !user.isActive()) throw new AuthorizationException("User with email:"
                + accountCredentials.email + " not found!");
        String encodePassword = tokenService.encodePassword(password);
        if (!user.getPassword().equals(encodePassword)) throw new AuthorizationException("User unauthorized!");
        Car car = carRepository.findById(serial_number).orElseThrow(() -> new NotFoundException("Car with serial number : "
                + serial_number + " not found!"));
        if (!user.getEmail().equals(car.getOwner().getEmail()))
            throw new ActionDeniedException("The user does not own the car");
        return ConvertersCars.createBookedPeriodDtoListFromBookedList(car.getBooked_periods());
    }

    //@author Dmitry
    @Override
    public CarForUsersDto getCarByIdForUsers(String serial_number) {
        Utils.usageStatistics("General", "getCarByIdForUsers");
        updateTrips();
        if (Utils.isValidSerialNumberAuto(serial_number)) throw new CarDetailsException("Serial number not valid");
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new NotFoundException("Car with serial_number : "
                        + serial_number + " not found!"));
        updateUserByComment(car);
        return ConvertersCars.createCarForUsersDtoFromCar(car
                , new ArrayList<>(ConvertersComments.createCommentDtoListFromCommentList(car.getComments())));

    }

    /*
     * @authors Dmitry, Anatoly
     */
    @Override
    public Iterable<CarForUsersDto> getThreePopularsCar() {
        Utils.usageStatistics("General", "getThreeBestBookedCars");
        updateTrips();
        List<Car> list = carRepository.getThreePopularsCar();
        HashMap<String, ArrayList<CommentDto>> mapComments = new HashMap<>();
        if (!list.isEmpty()) {
            for (Car car : list) {
                mapComments.put(car.getSerial_number()
                        , new ArrayList<>(ConvertersComments.createCommentDtoListFromCommentList(car.getComments())));
            }
            return ConvertersCars.createCarForUsersDtoListFromCarList(list, mapComments);
        }
        throw new NotFoundException("No populars cars!");
    }

    public CarLastOrderDto getLastOrder(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "getLastOrder");
        User user = userRepository.findUserByEmail(accountCredentials.email);
        if (user == null || !user.isActive()) throw new NotFoundException("User with email: "
                + accountCredentials.email + " not found!");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        if (user.getBookedCars().isEmpty())
            throw new NotFoundException("The user has not booked any cars yet");
        BookedCars bookedCars = user.getBookedCars().get(user.getBookedCars().size() - 1);
        Car car = carRepository.findById(bookedCars.getSerial_number())
                .orElseThrow(() -> new NotFoundException("Car deleted from repository"));
        return ConvertersCars.createCarLastOrderDtoFromCarAndBookedPeriod(car
                , bookedCars.getBooked_period());
    }

    @Override
    public Iterable<CarFullDto> getFiveFavoritesCars(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "getFiveFavoritesCars");
        User user = userRepository.findUserByEmail(accountCredentials.email);
        if (user == null || !user.isActive()) throw new NotFoundException("User with email: "
                + accountCredentials.email + " not found!");
        String encodePassword = tokenService.encodePassword(accountCredentials.password);
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        Map<String, Integer> map = new HashMap<>();
        ArrayList<BookedCars> bookedCars = user.getBookedCars();
        List<String> carNumbers = new ArrayList<>();
        bookedCars.forEach(c -> carNumbers.add(c.getSerial_number()));
        carNumbers.forEach(num -> map.put(num, map.get(num) == null ? 1 : map.get(num) + 1));
        int size = 5;
        if (map.size() < 5) {
            size = map.size();
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int max = Collections.max(map.values());
            for (Map.Entry<String, Integer> m : map.entrySet()) {
                if (m.getValue() == max) {
                    res.add(m.getKey());
                    map.put(m.getKey(), 0);
                    break;
                }
            }
        }
        return ConvertersCars.createCarFullDtoListFromCarList(carRepository.getCarsByNumberList(res));
    }

    private void updateTrips() {
        for (Car car : carRepository.findAll()) {
            int trip = car.getStatistics().getTrips();
            int tripNew = 0;
            if (car.getBooked_periods().size() > trip) {
                for (BookedPeriod bookedPeriod : car.getBooked_periods()) {
                    if ((bookedPeriod.getEnd_date_time()
                            .isBefore(LocalDateTime.now().plusHours(Utils.correctionTimeZone(
                                    car.getPick_up_place().getGeolocation().getLongitude()))))
                            && bookedPeriod.isPaid()) {
                        tripNew++;
                    }
                }
                if (tripNew > trip) {
                    car.getStatistics().setTrips(tripNew);
                    User owner = userRepository.findUserByEmail(car.getOwner().getEmail());
                    Car carOwner = owner.getOwnerCars().stream().filter(c -> c.getSerial_number()
                            .equals(car.getSerial_number())).findFirst().orElseThrow(() -> new NotFoundException(""));
                    carOwner.getStatistics().setTrips(tripNew);
                    carRepository.save(car);
                    userRepository.save(owner);
                }
            }
        }
    }

    private void ifLocationZero(Car car) {
        double latitude;
        double longitude;
        latitude = 32.071208;
        longitude = 34.780613;
        car.getPick_up_place().getGeolocation().setLatitude(latitude);
        car.getPick_up_place().getGeolocation().setLongitude(longitude);
    }

    private void updateUserByComment(Car car) {
        car.getComments().forEach(c -> {
            User userComment = userRepository.findUserByEmail(c.getEmail());
            if (!c.getPhoto().equals(userComment.getAvatar()))
                c.setPhoto(userComment.getAvatar());
            if (!c.getFirst_name().equals(userComment.getFirstName()))
                c.setFirst_name(userComment.getFirstName());
            if (!c.getSecond_Name().equals(userComment.getSecondName()))
                c.setSecond_Name(userComment.getSecondName());
        });
    }
}
