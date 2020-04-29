package com.telran.ilcarro.model.dto.comment;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class CommentDto {
    private String first_name;
    private String second_name;
    private String post_date;
    private String post;
    private String photo;
}
