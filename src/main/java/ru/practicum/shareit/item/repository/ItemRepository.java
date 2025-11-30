package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long userId);

    @Query("""
            SELECT i FROM Item i
            WHERE i.isAvailable = true
            AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))
            OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))
            """)
    Collection<Item> search(String text);

    Collection<Item> findAllByRequestId(Long requestId);

    Collection<Item> findAllByRequestIdIn(List<Long> ids);
}
