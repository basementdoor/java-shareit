package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDtoWithBookings> getAllByUser(Long userId);

    ItemDto getById(Long userId, Long id);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, UpdateItemDto item);

    Collection<ItemDto> search(String text);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentRequest);
}
