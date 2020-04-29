package com.telran.ilcarro.service;

import com.telran.ilcarro.exception.ActionDeniedException;
import com.telran.ilcarro.exception.AuthorizationException;
import com.telran.ilcarro.exception.CarDetailsException;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.model.documents.Car;
import com.telran.ilcarro.model.documents.Comment;
import com.telran.ilcarro.model.documents.Owner;
import com.telran.ilcarro.model.documents.User;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.model.dto.comment.CommentPostDto;
import com.telran.ilcarro.repository.CarRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.interfaces.CommentService;
import com.telran.ilcarro.service.interfaces.TokenService;
import com.telran.ilcarro.utility.ConvertersComments;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CommentServiceImpl implements CommentService {
    private CarRepository carRepository;
    private TokenService tokenService;
    private UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CarRepository carRepository
            , TokenService tokenService
            , UserRepository userRepository) {
        this.carRepository = carRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    //@Anatoly
    @Override
    public CommentDto addComment(String serial_number, String token, CommentPostDto content, int rating) {
        AccountCredentials accountCredentials = tokenService.decodeToken(token);
        Utils.usageStatistics(accountCredentials.email, "addNewCommentByCarId");
        try {
            if (Utils.isValidSerialNumberAuto(serial_number))
                throw new CarDetailsException("No valid serial number");
            if (content == null || content.getPost() == null || content.getPost().isEmpty())
                throw new NullPointerException("Content cannot be empty or null");

            String email = accountCredentials.email;
            String password = accountCredentials.password;
            User user = userRepository.findUserByEmail(email);
            if (user == null || !user.isActive())
                throw new NotFoundException("User by email: " + email + " not found");
            String encodePassword = tokenService.encodePassword(password);
            if (!user.getPassword().equals(encodePassword))
                throw new AuthorizationException("Wrong password! User unauthorized");
            Car car = carRepository.findById(serial_number).orElse(null);
            if (car == null)
                throw new NotFoundException("Car with serial number: " + serial_number + " not found");
            if (isUserUsedAuto(user, serial_number))
                throw new ActionDeniedException("Car number: " + serial_number + " forbidden be commented this user");
            Owner owner = car.getOwner();
            User userOwner = userRepository.findUserByEmail(owner.getEmail());
            ArrayList<Comment> commentsList = userOwner.getComments();
            Comment comment = ConvertersComments.createNewComment(user, content, serial_number);
            commentsList.add(comment);
            User updateUser = ConvertersComments.updateUserFromUserAndCommentList(userOwner, commentsList);
            car.getComments().add(comment);
            carRepository.save(car);
            userRepository.save(updateUser);
            return ConvertersComments.createCommentDtoFromComment(comment);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new AuthorizationException(ex.getMessage());
        } catch (CarDetailsException ex) {
            throw new CarDetailsException(ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ActionDeniedException(ex.getMessage());
        }
    }

    @Override
    public Iterable<CommentDto> getLatestComments() {
        Utils.usageStatistics("General", "getLatestComments");
        try {
            List<Comment> resList = new ArrayList<>();
            Iterable<Car> cars = carRepository.getThreePopularsCar();
            for (Car car : cars) {
                List<Comment> commentList = car.getComments();
                if (commentList.size() >= 2) {
                    commentList = commentList.subList(commentList.size() - 2, commentList.size());
                }
                resList.addAll(commentList);
            }
            resList.forEach(c -> {
                User user = userRepository.findUserByEmail(c.getEmail());
                if (user != null) {
                    if (!c.getPhoto().equals(user.getAvatar()))
                        c.setPhoto(user.getAvatar());
                    if (!c.getFirst_name().equals(user.getFirstName()))
                        c.setFirst_name(user.getFirstName());
                    if (!c.getSecond_Name().equals(user.getSecondName()))
                        c.setSecond_Name(user.getSecondName());
                }
            });
            return ConvertersComments.createCommentDtoListFromCommentList(resList);
        } catch (NullPointerException ex) {
            throw new NotFoundException("Not found comments");
        }
    }

    private boolean isUserUsedAuto(User user, String serial_number) {
        return user.getBookedCars().stream().filter(bookedCars -> bookedCars.getSerial_number()
                .equals(serial_number)).findFirst().isEmpty();
    }
}
