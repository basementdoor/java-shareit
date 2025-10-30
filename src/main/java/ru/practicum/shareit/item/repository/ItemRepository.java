package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getById(Long itemId);

    Item create(Item item);

    Item update(Item item);

    void delete(Long itemId);

    Collection<Item> getByUserId(Long id);

    Collection<Item> searchItems(String query);
}
