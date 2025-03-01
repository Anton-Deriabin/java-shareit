package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.item i " +
            "JOIN FETCH c.author " +
            "WHERE i.owner.id = :ownerId")
    List<Comment> findCommentsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id = :itemId")
    List<Comment> findCommentsByItemId(@Param("itemId") Long itemId);
}
