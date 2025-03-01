package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}