package com.telran.ilcarro.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.telran.ilcarro.exception.ActionDeniedException;
import com.telran.ilcarro.exception.AuthorizationException;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.exception.RegistrationModelException;
import com.telran.ilcarro.model.documents.*;
import com.telran.ilcarro.model.dto.user.BookedCarsDtoForUser;
import com.telran.ilcarro.model.dto.user.RegistrationDto;
import com.telran.ilcarro.model.dto.user.UserBaseDto;
import com.telran.ilcarro.model.dto.user.UserDtoForUser;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.interfaces.TokenService;
import com.telran.ilcarro.service.interfaces.UserService;
import com.telran.ilcarro.utility.ConvertersUsers;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private TokenService tokenService;
    private CarRepository carRepository;
    private MailSender mailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository
            , TokenService tokenService
            , CarRepository carRepository
            , MailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.carRepository = carRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDtoForUser addUser(RegistrationDto registrationDto, String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "registrationUser");
        if (registrationDto.getFirst_name().length() <= 1)
            throw new RegistrationModelException("First name must contain at least 2 letters");
        if (registrationDto.getSecond_name().length() <= 1)
            throw new RegistrationModelException("Second name must contain at least 2 letters");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        User user = userRepository.findUserByEmail(email);
        if (user != null && user.isActive()) {
            throw new AuthorizationException("User with email: " + email + " already Exist");
        } else if (user != null && !user.isActive()) {
            throw new ActionDeniedException("User with email: " + email
                    + " is forbidden to register. Contact to tech support");
        }
        UserDtoForUser userDtoForUser = ConvertersUsers.userDtoFromRegistrationDto(registrationDto);
        userRepository.save(ConvertersUsers.createUserFromUserDto(userDtoForUser, email, encodePassword));
        //mailSender.send(email,"Activation code","Please visit this link for activation account : "
        //+ "http://java-test-ilcarro-team-b.herokuapp.com/activate/"
        // +"http://localhost:8090/activate/"
        //  + userRepository.findUserByEmail(email).getActivationCode());
        return userDtoForUser;
    }

    @Override
    public UserDtoForUser updateUser(String newPassword, String token, UserBaseDto userBaseDto) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "updateUser");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        User user = userRepository.findUserByEmail(email);
        if (user == null || !user.isActive())
            throw new NotFoundException("User with email: " + email + " not found!");
        if (!user.getPassword().equals(encodePassword))
            throw new AuthorizationException("User with email: " + email + " not authorized!");
        if (userBaseDto.getFirst_name() == null || userBaseDto.getFirst_name().isEmpty())
            userBaseDto.setFirst_name(user.getFirstName());
        if (userBaseDto.getFirst_name().length() == 1)
            throw new RegistrationModelException("Bad new first name");
        if (userBaseDto.getSecond_name() == null || userBaseDto.getSecond_name().isEmpty())
            userBaseDto.setSecond_name(user.getSecondName());
        if (userBaseDto.getSecond_name().length() == 1)
            throw new RegistrationModelException("Bad new second name");
        if (userBaseDto.getPhoto() == null || userBaseDto.getPhoto().isEmpty()) userBaseDto.setPhoto(user.getAvatar());
        if (userBaseDto.getPhoto().length() > 0 && userBaseDto.getPhoto().length() < 6)
            throw new RegistrationModelException("Bad new src photo");
        if (newPassword == null) newPassword = encodePassword;
        if (Utils.isValidPassword(tokenService.decodePassword(newPassword)))
            throw new RegistrationModelException("Bad new password");
        Owner ownerCar = ConvertersUsers.updateOwnerFromUserBaseDtoAndUser(userBaseDto, user);
        List<Car> ownerCars = carRepository.findCarsByOwnerEmail(user.getEmail());
        ownerCars.forEach(car -> car.setOwner(ownerCar));
        ownerCars.forEach(car -> carRepository.save(car));
        UserDtoForUser newUserDtoForUser = ConvertersUsers.createUserDtoFromUserBaseDtoAndUser(userBaseDto, user);
        User updateUser = ConvertersUsers.updateUserFromUserAndUserDtoForUserAndListCars(
                user, newPassword, newUserDtoForUser, ownerCars);
        userRepository.save(updateUser);
        return newUserDtoForUser;
    }

    @Override
    public void deleteUser(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "deleteUser");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        String encodePassword = tokenService.encodePassword(password);
        User user = userRepository.findUserByEmail(email);
        if (user == null || !user.isActive()) throw new NotFoundException("User not found!");
        if (!user.getPassword().equals(encodePassword)) throw new AuthorizationException("Wrong password!");
        List<Car> ownerCars = user.getOwnerCars();
        if (!ownerCars.isEmpty()) {
            for (Car car : ownerCars) {
                for (BookedPeriod bookedPeriod : car.getBooked_periods()) {
                    if (bookedPeriod.getEnd_date_time().isAfter(LocalDateTime.now()
                            .plusHours(Utils.correctionTimeZone(car.getPick_up_place()
                                    .getGeolocation().getLongitude())))) {
                        throw new ActionDeniedException("You cannot delete a user because he has " +
                                "a car with active reservation (Car number: " + car.getSerial_number() + ")");
                    }
                }
            }
        }
        List<BookedCars> bookedCars = user.getBookedCars();
        if (!bookedCars.isEmpty()) {
            for (BookedCars car : bookedCars) {
                Car c = carRepository.findById(car.getSerial_number()).orElse(null);
                if (c != null) {
                    if (car.getBooked_period().getEnd_date_time().isAfter(LocalDateTime.now()
                            .plusHours(Utils.correctionTimeZone(c.getPick_up_place()
                                    .getGeolocation().getLongitude())))) {
                        throw new ActionDeniedException("You cannot delete a user because " +
                                "he has an active reservation for a car (Car number: "
                                + c.getSerial_number() + ")");
                    }
                }
            }
        }
        user.setOwnerCars(new ArrayList<>());
        user.setActive(false);
        userRepository.save(user);
        carRepository.deleteAll(ownerCars);
    }

    @Override
    public UserDtoForUser authUser(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "authUser");
        String email = accountCredentials.email;
        String password = accountCredentials.password;
        User user = userRepository.findUserByEmail(email);
        String encodePassword = tokenService.encodePassword(password);
        if (user == null || !user.isActive()) throw new NotFoundException("User not found!");
        if (!user.getPassword().equals(encodePassword)) throw new AuthorizationException("Wrong password!");
        return ConvertersUsers.createUserDtoFromUser(user);
    }

    @Override
    public String activateUser(String code) {
        Utils.usageStatistics("General", "activateUser");
        String MESSAGE = "is activated";
        List<User> list = userRepository.findUserByActivationCode(code);
        if (list.isEmpty()) throw new NotFoundException("Activation code not found!");
        list.get(0).setActivationCode(MESSAGE);
        userRepository.save(list.get(0));
        return MESSAGE;
    }

    @Override
    public void remindPassword(String email) {
        Utils.usageStatistics("General", "remindPassword");
        if (email == null || Utils.isValidEmail(email)) throw new NotFoundException("No valid email!");
        User user = userRepository.findUserByEmail(email);
        if (user == null) throw new NotFoundException("User with email: " + email + " not found!");
        String password = tokenService.decodePassword(user.getPassword());
        mailSender.send(email, "Your password from Ilcarro!", "Your password from Ilcarro service is: "
                + password);

    }

    @Override
    public void getInvoice(String token, HttpServletResponse response, String order_id)
            throws Exception {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "getInvoice");
        String email = accountCredentials.email;
        User user = userRepository.findUserByEmail(email);
        if (user == null) throw new NotFoundException("User with email: " + email + " not found!");
        BookedPeriod booked = user.getBookedCars().stream().map(BookedCars::getBooked_period)
                .filter(v -> v.getOrder_id().equals(order_id)).findFirst()
                .orElseThrow(() -> new NotFoundException("Order with " + order_id + " not found!"));
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(booked.getOrder_id() + ".pdf"));
        document.open();

        BufferedImage qrCode = Utils.generateQRCodeImage("Order ID: " + booked.getOrder_id());

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font font2 = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        Chunk chunk = new Chunk("Invoice from ilcarro!", font);
        Paragraph paragraph = new Paragraph("Order ID: " + booked.getOrder_id() + "\n"
                + booked.getPerson_who_booked() + "\n"
                + booked.getStart_date_time() + "\n"
                + booked.getEnd_date_time() + "\n"
                + booked.getAmount() + "\n"
                , font2);
        document.add(chunk);
        document.add(paragraph);

        File outputFile = new File(booked.getOrder_id() + ".jpeg");
        ImageIO.write(qrCode, "jpeg", outputFile);
        Image img = Image.getInstance(outputFile.getAbsolutePath());
        document.add(img);
        document.close();
        Path file = Paths.get(booked.getOrder_id() + ".pdf");
        if (Files.exists(file)) {
            response.setHeader("Content-disposition", "attachment; filename=" + booked.getOrder_id() + ".pdf");
            response.setContentType("application/pdf");
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
                Files.deleteIfExists(Paths.get(booked.getOrder_id() + ".pdf"));
                Files.deleteIfExists(Paths.get(booked.getOrder_id() + ".jpeg"));
            } catch (IOException e) {
                throw new NotFoundException("File is broken!");
            }
        }
    }

    @Override
    public Iterable<BookedCarsDtoForUser> getHistory(String token) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "getHistory");
        String email = accountCredentials.email;
        User user = userRepository.findUserByEmail(email);
        if (user == null) throw new NotFoundException("User with email: " + email + " not found!");
        if (user.getBookedCars().isEmpty()) throw new NotFoundException("History list not found!");
        return ConvertersUsers.createHistoryList(user.getBookedCars());
    }
}
