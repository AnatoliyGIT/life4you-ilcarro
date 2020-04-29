package com.telran.ilcarro.model.documents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "filter")
public class Filter {
    @Id
    private String id;
    private String filter_string;
}
