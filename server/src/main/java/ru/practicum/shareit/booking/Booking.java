package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"start", "end", "itemId", "bookerId"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "bookings")
@Getter
@Setter
@ToString
public class Booking {

    @Id
    String id;  // Mongo ObjectId

    LocalDateTime start;
    LocalDateTime end;

    @ToString.Exclude
    String itemId;

    @ToString.Exclude
    String bookerId;

    Status status;
}


