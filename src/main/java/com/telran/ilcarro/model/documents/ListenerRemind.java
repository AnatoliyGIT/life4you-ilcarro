package com.telran.ilcarro.model.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@Document(collection = "reminds")
public class ListenerRemind {
    @Id
    String email;
    List<LocalDateTime> remind_time;
}
