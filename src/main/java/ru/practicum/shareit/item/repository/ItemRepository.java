package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long userId);

    @Query("""
            select i from Item i
            where i.isAvailable = true
            and upper(i.name) like upper(concat('%', ?1, '%'))
            or upper(i.description) like upper(concat('%', ?1, '%'))
            """)
    Collection<Item> search(String text);

//    Optional<Item> getById(Long itemId);
//
//    Item create(Item item);
//
//    Item update(Item item, UpdateItemDto updateItem);
//
//    Collection<Item> getByUserId(Long id);
//
//    Collection<Item> searchItems(String text);
}
