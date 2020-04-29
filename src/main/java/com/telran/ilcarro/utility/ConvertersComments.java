package com.telran.ilcarro.utility;

import com.telran.ilcarro.model.documents.Comment;
import com.telran.ilcarro.model.documents.User;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.model.dto.comment.CommentPostDto;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ConvertersComments {

    public static ArrayList<CommentDto> createCommentDtoListFromCommentList(List<Comment> commentList) {

        ArrayList<CommentDto> listComments = new ArrayList<>();
        if (commentList != null) {
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

    public static Comment createNewComment(User user, CommentPostDto content, String serialNumber) {
        return Comment.builder()
                .email(user.getEmail())
                .second_Name(user.getSecondName())
                .photo(user.getAvatar())
                .first_name(user.getFirstName())
                .content(content.getPost())
                .post_date(LocalDate.now())
                .serial_number(serialNumber)
                .build();
    }

    public static User updateUserFromUserAndCommentList(User userOwner, ArrayList<Comment> commentsList) {
        return User.builder()
                .email(userOwner.getEmail())
                .password(userOwner.getPassword())
                .firstName(userOwner.getFirstName())
                .secondName(userOwner.getSecondName())
                .avatar(userOwner.getAvatar())
                .registrationDate(userOwner.getRegistrationDate())
                .comments(commentsList)
                .ownerCars(userOwner.getOwnerCars())
                .bookedCars(userOwner.getBookedCars())
                .history(userOwner.getHistory())
                .activationCode(userOwner.getActivationCode())
                .isActive(userOwner.isActive())
                .role(userOwner.getRole())
                .build();
    }
}