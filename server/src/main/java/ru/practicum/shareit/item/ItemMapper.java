package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        return dto;
    }

    public static ItemWithCommentsDto mapToItemWithCommentsDto(Item item, List<Comment> comments, List<User> authors) {
        ItemWithCommentsDto dto = new ItemWithCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());

        Map<String, String> authorNames = authors.stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        dto.setComments(comments.stream()
                .map(comment -> {
                    String authorName = authorNames.getOrDefault(comment.getAuthorId(), "Неизвестный автор");
                    return CommentMapper.mapToCommentDto(comment, authorName);
                })
                .collect(Collectors.toList()));

        return dto;
    }


    public static ItemWithBookingsCommentsDto mapToItemWithBookingsCommentsDto(
            Item item, List<Booking> bookings, List<Comment> comments, List<User> commentAuthors) {

        ItemWithBookingsCommentsDto dto = new ItemWithBookingsCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());

        dto.setBookings(bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList()));

        Map<String, String> authorNames = commentAuthors.stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        dto.setComments(comments.stream()
                .map(comment -> {
                    String authorName = authorNames.getOrDefault(comment.getAuthorId(), "Неизвестный автор");
                    return CommentMapper.mapToCommentDto(comment, authorName);
                })
                .collect(Collectors.toList()));

        return dto;
    }


    public static Item mapToItemFromCreateDto(ItemCreateDto itemDto, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(owner.getId());
        item.setRequestId(request.getId());
        return item;
    }

    public static Item mapToItemFromDto(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(owner.getId());
        return item;
    }

    public static ItemResponseDto mapToItemResponseDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwnerId());
        return dto;
    }
}
