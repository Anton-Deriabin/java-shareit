package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "name", "description", "ownerId" })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "items")
@Getter
@Setter
@ToString
public class Item {

    @Id
    String id;


    String name;


    String description;


    Boolean available;

    @ToString.Exclude
    String ownerId;

    @ToString.Exclude
    String requestId;
}

