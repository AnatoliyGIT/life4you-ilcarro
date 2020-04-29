package com.telran.ilcarro.model.dto.car;

import com.telran.ilcarro.model.dto.comment.CommentDto;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class UserDtoForCar {
    private String first_name;
    private String second_name;
    private String photo;
    private String registration_date;
    private ArrayList<CommentDto> comments;
    private ArrayList<CarFullDto> owner_cars;
    private ArrayList<BookedCarsDtoForCar> booked_cars;
    private ArrayList<HistoryDtoForCar> history;
}
