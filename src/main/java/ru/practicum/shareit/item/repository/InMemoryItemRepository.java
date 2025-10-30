package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.empty();
    }

    @Override
    public Item create(Item item) {
        return null;
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public void delete(Long itemId) {

    }

    @Override
    public Collection<Item> getByUserId(Long id) {
        return List.of();
    }

    @Override
    public Collection<Item> searchItems(String query) {
        return List.of();
    }
}
