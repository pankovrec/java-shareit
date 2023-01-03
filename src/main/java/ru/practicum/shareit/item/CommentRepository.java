package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Set;

/**
 * Comment repository.
 */

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> findCommentsByItem_Id(long itemId);
}