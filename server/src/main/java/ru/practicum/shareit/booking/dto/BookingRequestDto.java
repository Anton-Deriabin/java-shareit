package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
}
