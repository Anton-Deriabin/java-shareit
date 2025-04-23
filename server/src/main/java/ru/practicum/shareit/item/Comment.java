package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "text", "itemId", "authorId" })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "comments")
@Getter
@Setter
@ToString
public class Comment {

    @Id
    String id;

    String text;

    @ToString.Exclude
    String itemId;

    @ToString.Exclude
    String authorId;

    LocalDateTime created;
}
