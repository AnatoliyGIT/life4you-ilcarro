package com.telran.ilcarro.model.dto.filters;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class CommentDtoForFilters {
    private String first_name;
    private String second_name;
    private String post_date;
    private String post;
    private String photo;
}
