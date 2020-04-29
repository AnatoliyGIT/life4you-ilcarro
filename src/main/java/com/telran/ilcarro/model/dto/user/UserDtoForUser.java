package com.telran.ilcarro.model.dto.user;

import com.telran.ilcarro.model.dto.comment.CommentDto;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class UserDtoForUser {
    private String first_name;
    private String second_name;
    private String photo;
    private String registration_date;
    private ArrayList<CommentDto> comments;
    private ArrayList<OwnerCarDtoForUser> own_cars;
    private ArrayList<BookedCarsDtoForUser> booked_cars;
    private ArrayList<HistoryDtoForUser> history;
}
