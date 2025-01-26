package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "owner" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long request;
}
