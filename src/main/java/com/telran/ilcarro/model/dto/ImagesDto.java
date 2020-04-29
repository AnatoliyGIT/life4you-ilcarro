package com.telran.ilcarro.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ImagesDto {
    private List<ImageDto> images;
}
