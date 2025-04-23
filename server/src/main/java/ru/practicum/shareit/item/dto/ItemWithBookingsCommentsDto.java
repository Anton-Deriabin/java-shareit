package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsCommentsDto {
    String id;
    String name;
    String description;
    Boolean available;
    String ownerId;
    List<BookingDto> bookings;
    List<CommentDto> comments;
}