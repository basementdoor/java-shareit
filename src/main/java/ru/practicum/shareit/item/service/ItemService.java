package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getAllByUser(Long userId);

    ItemDto getById(Long userId, Long id);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, UpdateItemDto item);

    Collection<ItemDto> search(Long userId, String text);
}
