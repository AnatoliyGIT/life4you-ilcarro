package com.telran.ilcarro.model.documents;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class History {
    private String serial_number;
    private HistoryCars history;
}
