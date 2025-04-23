package ru.practicum.shareit.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "description"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "requests")
@Getter
@Setter
@ToString
public class ItemRequest {

    @Id
    String id;

    @Size(max = 1024)
    String description;

    LocalDateTime created;

    @ToString.Exclude
    String requestorId;
}

