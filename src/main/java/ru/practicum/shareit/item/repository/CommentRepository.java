package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.item.id = :itemId
            """)
    Collection<Comment> findAllByItemId(Long itemId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.item.id IN :itemIds
            """)
    Collection<Comment> findAllByItemIds(List<Long> itemIds);
}
