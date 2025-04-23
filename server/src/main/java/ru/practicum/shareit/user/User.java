package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "email" })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    String id;

    @Size(max = 255)
    String name;

    @Size(max = 512)
    String email;
}

