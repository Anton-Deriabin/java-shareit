package ru.practicum.shareit.booking;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByBookerId(String bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(String bookerId,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(String bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(String bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(String bookerId, Status status);

    List<Booking> findByItemIdInOrderByStartDesc(List<String> itemIds);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<String> itemIds,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<String> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<String> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<String> itemIds, Status status);
}
