package com.telran.ilcarro.utility;

import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.car.ReservedPeriodDto;
import com.telran.ilcarro.repository.UsageStatisticsDateRepository;
import com.telran.ilcarro.repository.UsageStatisticsRepository;
import com.telran.ilcarro.service.AccountCredentials;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    public static Pattern pattern;
    public static Matcher matcher;


    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isValidEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        int res = 0;
        if (password.length() >= 8) {
            res = res | 1; // 00001
        }
        char[] arr = password.toCharArray();
        for (char c : arr) {
            if (Character.isUpperCase(c)) {
                res = res | 2; // 00010
            }
            if (Character.isLowerCase(c)) {
                res = res | 4; // 00100
            }
            if (Character.isDigit(c)) {
                res = res | 8; // 01000
            }
        }
        return res != 15;
    }

    public static boolean isValidSerialNumberAuto(String sn) throws CarDetailsException {
        char[] charArr = sn.toCharArray();
        char[] specSym = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '~', '+', ':', ';'};
        if (sn.length() < 7 || sn.length() > 12) {
            return true;
        }
        for (char s : charArr) {
            for (char value : specSym) {
                if (s == value) {
                    return true;
                }
            }
        }
        return false;
    }

//    public static void validationPrice(CarFullUploadRequestDto dto) {
//        try {
//            Double.valueOf(dto.getPrice_per_day());
//        } catch (NumberFormatException ex) {
//            throw new IllegalArgumentException("Price is not number");
//        }
//        if (dto.getPrice_per_day() <= 0) {
//            throw new IllegalArgumentException("Price is empty");
//        }
//    }

    public static boolean validationBookedPeriods(LocalDateTime start_date_time, LocalDateTime end_date_time, Car car) {
        if (car.getBooked_periods().isEmpty() && car.getReserved_periods().isEmpty()) return true;
        Optional<BookedPeriod> bookedPeriodDtoUser = car.getBooked_periods().stream().filter(carDate ->
                ((start_date_time).isEqual(carDate.getStart_date_time())
                        || ((end_date_time).isEqual(carDate.getEnd_date_time())))
                        || (((start_date_time).isBefore(carDate.getEnd_date_time()))
                        && ((start_date_time).isAfter(carDate.getStart_date_time())))
                        || (((end_date_time).isBefore(carDate.getEnd_date_time()))
                        && ((end_date_time).isAfter(carDate.getStart_date_time())))
        ).findFirst();
        Optional<BookedPeriod> bookedPeriodDtoCar = car.getBooked_periods().stream().filter(carDate ->
                (carDate.getStart_date_time()).isAfter(start_date_time)
                        && (carDate.getStart_date_time().isBefore(end_date_time))
        ).findFirst();
        Optional<ReservedPeriod> reservedPeriodDtoUser = car.getReserved_periods().stream().filter(period ->
                ((start_date_time).isEqual(period.getStart_date_time())
                        || ((end_date_time).isEqual(period.getEnd_date_time())))
                        || (((start_date_time).isBefore(period.getEnd_date_time()))
                        && ((start_date_time).isAfter(period.getStart_date_time())))
                        || (((end_date_time).isBefore(period.getEnd_date_time()))
                        && ((end_date_time).isAfter(period.getStart_date_time())))
        ).findFirst();
        Optional<ReservedPeriod> reservedPeriodDtoCar = car.getReserved_periods().stream().filter(carDate ->
                (carDate.getStart_date_time()).isAfter(start_date_time)
                        && (carDate.getStart_date_time().isBefore(end_date_time))
        ).findFirst();
        return (!bookedPeriodDtoUser.isPresent() && !bookedPeriodDtoCar.isPresent()
                && !reservedPeriodDtoCar.isPresent() && !reservedPeriodDtoUser.isPresent());
    }

    public static boolean validationReservedPeriods(ArrayList<ReservedPeriod> reservedPeriods, Car car) {
        for (ReservedPeriod reservedPeriod : reservedPeriods) {
            if (!validationBookedPeriods(reservedPeriod.getStart_date_time()
                    , reservedPeriod.getEnd_date_time(), car)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUserAndCar(AccountCredentials accountCredentials
            , String encodePassword, User user, Car car) {
        String email = accountCredentials.email;
        if (user == null || !user.isActive())
            throw new NotFoundException("User with email: " + email + " not found");
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("Wrong password! User unauthorized");
        if (car == null)
            throw new NotFoundException("Car not found!");
        if (isValidSerialNumberAuto(car.getSerial_number()))
            throw new CarDetailsException("No valid serial number");
        if (!car.getOwner().getEmail().equals(user.getEmail()))
            throw new ActionDeniedException("User with email: " + email + " does not own a car");
        return false;
    }

    public static long getHoursTotalBetweenTwoDates(LocalDateTime start_Date_Time, LocalDateTime end_Date_Time) {
        LocalDateTime fromDateTime = LocalDateTime.of(start_Date_Time.getYear()
                , start_Date_Time.getMonth()
                , start_Date_Time.getDayOfMonth()
                , start_Date_Time.getHour()
                , start_Date_Time.getMinute());
        LocalDateTime toDateTime = LocalDateTime.of(end_Date_Time.getYear()
                , end_Date_Time.getMonth()
                , end_Date_Time.getDayOfMonth()
                , end_Date_Time.getHour()
                , end_Date_Time.getMinute());
        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);
        return tempDateTime.until(toDateTime, ChronoUnit.HOURS);
    }

    public static int correctionTimeZone(Double l) {
        int longitude = (int) Math.floor(l);
        return Math.floorDiv(longitude, 15);
    }

    public static void isValidFormatDateAndTime(String start_date_time, String end_date_time) {
        try {
            LocalDateTime.parse(start_date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime.parse(end_date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException exception) {
            throw new RequestArgumentsException("Wrong date format! (yyyy-MM-dd HH:mm)");
        }
    }

    public static String correctionDateAndTime(String date_time) {
        if (date_time.isEmpty())
            throw new RequestArgumentsException("All fields must be filled");
        String dateTime = date_time;
        String date;
        String time;
        try {
            int indexStart = dateTime.indexOf(" ");
            if (indexStart == -1) {
                dateTime = dateTime + " "
                        + LocalTime.now(ZoneId.of("Asia/Jerusalem"))
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            if (indexStart != -1 && dateTime.length() < 16) {
                date = dateTime.substring(0, indexStart);
                time = dateTime.substring(indexStart + 1);
                dateTime = date + " 0" + time;
            }
            System.out.println(dateTime);
            return dateTime;
        } catch (DateTimeParseException ex) {
            throw new CarDetailsException("Wrong dates and times (yyyy-MM-dd HH:mm)");
        }
    }

    //QR CODE GENERATOR
    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        ByteArrayOutputStream stream = QRCode
                .from(barcodeText)
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }

    public static void usageUserStatistics(UsageStatistics statistics) {
        String time = LocalDateTime.now(ZoneId.of("Asia/Jerusalem")).toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        String date = LocalDateTime.now(ZoneId.of("Asia/Jerusalem")).minusDays(1).toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String email = statistics.getEmail();
        UsageStatistics usageStatistics;
        if (StatisticsDate.statisticsDateRepository.findByDate(date) == null
                || StatisticsDate.statisticsDateRepository.findByDate(date).getUsageStatisticsList().isEmpty()) {
            List<UsageStatistics> usageStatisticsYesterdayList = new ArrayList<>(Statistics
                    .statisticsRepository.findAll());
            System.out.println(StatisticsDate.statisticsDateRepository.findByDate(date));
            UsageStatisticsYesterday usageStatisticsYesterday = new UsageStatisticsYesterday(date
                    , usageStatisticsYesterdayList);
            StatisticsDate.statisticsDateRepository.save(usageStatisticsYesterday);
            Statistics.statisticsRepository.deleteAll();
        }
        if (Statistics.statisticsRepository.findUsageStatisticsByEmail("General") == null) {
            Statistics.statisticsRepository.save(new UsageStatistics("0", "General"
                    , new ObjectUserStatistics(), new ObjectGeneralStatistics()));
        }
        if (Statistics.statisticsRepository.findUsageStatisticsByEmail(email) == null) {
            usageStatistics = new UsageStatistics("0", email, new ObjectUserStatistics(), new ObjectGeneralStatistics());
        } else {
            usageStatistics = Statistics.statisticsRepository.findUsageStatisticsByEmail(email);
        }
        ObjectUserStatistics os = usageStatistics.getObjectUserStatistics();
        ObjectUserStatistics userStatistics = ObjectUserStatistics.builder()
                .addNewCar(os.getAddNewCar() + statistics.getObjectUserStatistics().getAddNewCar())
                .addNewCommentByCarId(os.getAddNewCommentByCarId() + statistics.getObjectUserStatistics().getAddNewCommentByCarId())
                .authUser(os.getAuthUser() + statistics.getObjectUserStatistics().getAuthUser())
                .deleteCar(os.getDeleteCar() + statistics.getObjectUserStatistics().getDeleteCar())
                .getBookedList(os.getGetBookedList() + statistics.getObjectUserStatistics().getGetBookedList())
                .deleteUser(os.getDeleteUser() + statistics.getObjectUserStatistics().getDeleteUser())
                .getInvoice(os.getGetInvoice() + statistics.getObjectUserStatistics().getGetInvoice())
                .lockCarForBookingCarId(os.getLockCarForBookingCarId() + statistics.getObjectUserStatistics().getLockCarForBookingCarId())
                .makeReservation(os.getMakeReservation() + statistics.getObjectUserStatistics().getMakeReservation())
                .ownerGetBookedPeriodByCarId(os.getOwnerGetBookedPeriodByCarId() + statistics.getObjectUserStatistics().getOwnerGetBookedPeriodByCarId())
                .ownerGetCarBySerialNumber(os.getOwnerGetCarBySerialNumber() + statistics.getObjectUserStatistics().getOwnerGetCarBySerialNumber())
                .ownerGetCars(os.getOwnerGetCars() + statistics.getObjectUserStatistics().getOwnerGetCars())
                .paymentForReservation(os.getPaymentForReservation() + statistics.getObjectUserStatistics().getPaymentForReservation())
                .registrationUser(os.getRegistrationUser() + statistics.getObjectUserStatistics().getRegistrationUser())
                .reservationCancellation(os.getReservationCancellation() + statistics.getObjectUserStatistics().getReservationCancellation())
                .unlockCarForBookingCarId(os.getUnlockCarForBookingCarId() + statistics.getObjectUserStatistics().getUnlockCarForBookingCarId())
                .updateCar(os.getUpdateCar() + statistics.getObjectUserStatistics().getUpdateCar())
                .updateUser(os.getUpdateUser() + statistics.getObjectUserStatistics().getUpdateUser())
                .getHistory(os.getGetHistory() + statistics.getObjectUserStatistics().getGetHistory())
                .getFiveFavoritesCars(os.getGetFiveFavoritesCars() + statistics.getObjectUserStatistics().getGetFiveFavoritesCars())
                .getLastOrder(os.getGetLastOrder() + statistics.getObjectUserStatistics().getGetLastOrder())
                .build();
        UsageStatistics newUsageStatistics = UsageStatistics.builder()
                .time(time)
                .email(email)
                .objectUserStatistics(userStatistics)
                .build();
        Statistics.statisticsRepository.save(newUsageStatistics);
    }

    public static void usageGeneralStatistics(UsageStatistics statistics) {
        String time = LocalDateTime.now(ZoneId.of("Asia/Jerusalem")).toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        String date = LocalDateTime.now(ZoneId.of("Asia/Jerusalem")).minusDays(1).toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String general = "General";
        UsageStatistics usageStatistics;
        if (StatisticsDate.statisticsDateRepository.findByDate(date) == null
                || StatisticsDate.statisticsDateRepository.findByDate(date).getUsageStatisticsList().isEmpty()) {
            List<UsageStatistics> usageStatisticsYesterdayList = new ArrayList<>(Statistics
                    .statisticsRepository.findAll());
            UsageStatisticsYesterday usageStatisticsYesterday = new UsageStatisticsYesterday(date
                    , usageStatisticsYesterdayList);
            StatisticsDate.statisticsDateRepository.save(usageStatisticsYesterday);
            Statistics.statisticsRepository.deleteAll();
        }
        if (Statistics.statisticsRepository.findUsageStatisticsByEmail(general) == null) {
            usageStatistics = new UsageStatistics("0", general, new ObjectUserStatistics(), new ObjectGeneralStatistics());
        } else {
            usageStatistics = Statistics.statisticsRepository.findUsageStatisticsByEmail(general);
        }
        ObjectGeneralStatistics objectGeneralStatistics = usageStatistics.getObjectGeneralStatistics();
        ObjectGeneralStatistics generalStatistics = ObjectGeneralStatistics.builder()
                .activateUser(objectGeneralStatistics.getActivateUser() + statistics.getObjectGeneralStatistics().getActivateUser())
                .downloadImages(objectGeneralStatistics.getDownloadImages() + statistics.getObjectGeneralStatistics().getDownloadImages())
                .getCarByDateLocationAndPrice(objectGeneralStatistics.getGetCarByDateLocationAndPrice() + statistics.getObjectGeneralStatistics().getGetCarByDateLocationAndPrice())
                .getCarByLocation(objectGeneralStatistics.getGetCarByLocation() + statistics.getObjectGeneralStatistics().getGetCarByLocation())
                .getCarByFilter(objectGeneralStatistics.getGetCarByFilter() + statistics.getObjectGeneralStatistics().getGetCarByFilter())
                .getCarByIdForUsers(objectGeneralStatistics.getGetCarByIdForUsers() + statistics.getObjectGeneralStatistics().getGetCarByIdForUsers())
                .getFilters(objectGeneralStatistics.getGetFilters() + statistics.getObjectGeneralStatistics().getGetFilters())
                .getLatestComments(objectGeneralStatistics.getGetLatestComments() + statistics.getObjectGeneralStatistics().getGetLatestComments())
                .getLoggers(objectGeneralStatistics.getGetLoggers() + statistics.getObjectGeneralStatistics().getGetLoggers())
                .getThreeBestBookedCars(objectGeneralStatistics.getGetThreeBestBookedCars() + statistics.getObjectGeneralStatistics().getGetThreeBestBookedCars())
                .remindPassword(objectGeneralStatistics.getRemindPassword() + statistics.getObjectGeneralStatistics().getRemindPassword())
                .searchThatContainsAllSearches(objectGeneralStatistics.getSearchThatContainsAllSearches() + statistics.getObjectGeneralStatistics().getSearchThatContainsAllSearches())
                .updateTimerFilters(objectGeneralStatistics.getUpdateTimerFilters() + statistics.getObjectGeneralStatistics().getUpdateTimerFilters())
                .build();
        UsageStatistics newUsageStatistics = UsageStatistics.builder()
                .time(time)
                .email(general)
                .objectGeneralStatistics(generalStatistics)
                .build();
        Statistics.statisticsRepository.save(newUsageStatistics);
    }

    public static ArrayList<String> imageUrlDefault(ArrayList<String> imageUrl) {
        ArrayList<String> list = new ArrayList<>();
        if (imageUrl.size() == 1 && imageUrl.get(0).equals("") || imageUrl.isEmpty()) {
            list.add("http://res.cloudinary.com/dvde7hpxw/image/upload/v1586969453/gaks7nojgrxggx6eyasj.jpg");
        } else {
            list.addAll(imageUrl);
        }
        return list;
    }

    public static void usageStatistics(String email, String nameMethod) {
        try {
            ObjectUserStatistics ous = new ObjectUserStatistics();
            ObjectGeneralStatistics ogs = new ObjectGeneralStatistics();
            if (email.equals("General")) {
                UsageStatistics us = new UsageStatistics("0", "General", ous, ogs);
                Field field = ogs.getClass().getDeclaredField(nameMethod);
                field.setAccessible(true);
                field.setInt(ogs, 1);
                field.setAccessible(false);
                us.setObjectGeneralStatistics(ogs);
                usageGeneralStatistics(us);
            } else {
                UsageStatistics us = new UsageStatistics("0", email, ous, ogs);
                Field field = ous.getClass().getDeclaredField(nameMethod);
                field.setAccessible(true);
                field.setInt(ous, 1);
                field.setAccessible(false);
                us.setObjectUserStatistics(ous);
                usageUserStatistics(us);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error");
        }
    }

    public static ArrayList<ReservedPeriodDto> correctionDateAndTime(ArrayList<ReservedPeriodDto> dtoArrayList) {
        ArrayList<ReservedPeriodDto> list = new ArrayList<>();
        for (ReservedPeriodDto period : dtoArrayList) {
            period.setStart_date_time(Utils.correctionDateAndTime(period.getStart_date_time()));
            period.setEnd_date_time(Utils.correctionDateAndTime(period.getEnd_date_time()));
            Utils.isValidFormatDateAndTime(period.getStart_date_time(), period.getEnd_date_time());
            list.add(period);
        }
        return list;
    }

    @Repository
    public static class Statistics {
        public static UsageStatisticsRepository statisticsRepository;

        public Statistics(UsageStatisticsRepository statisticsRepository) {
            Statistics.statisticsRepository = statisticsRepository;
        }
    }

    @Repository
    public static class StatisticsDate {
        public static UsageStatisticsDateRepository statisticsDateRepository;

        public StatisticsDate(UsageStatisticsDateRepository statisticsDateRepository) {
            StatisticsDate.statisticsDateRepository = statisticsDateRepository;
        }
    }
}
