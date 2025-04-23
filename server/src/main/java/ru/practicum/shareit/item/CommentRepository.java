package ru.practicum.shareit.item;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByItemId(String itemId);

    List<Comment> findByItemIdIn(List<String> itemIds);
}
