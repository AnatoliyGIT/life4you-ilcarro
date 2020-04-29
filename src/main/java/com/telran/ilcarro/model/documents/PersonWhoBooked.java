package com.telran.ilcarro.model.documents;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PersonWhoBooked {
    private String email;
    private String first_name;
    private String second_name;
    private String phone;
}
