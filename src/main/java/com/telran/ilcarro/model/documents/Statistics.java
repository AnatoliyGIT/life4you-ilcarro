package com.telran.ilcarro.model.documents;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Statistics {
    private Integer trips;
    private Integer rating;
}
