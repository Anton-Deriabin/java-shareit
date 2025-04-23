package ru.practicum.shareit.request;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends MongoRepository<ItemRequest, String> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(String requestorId);
}

