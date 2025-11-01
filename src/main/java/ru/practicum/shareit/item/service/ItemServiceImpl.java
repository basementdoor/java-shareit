package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAllByUser(Long userId) {
        throwIfUserNotExist(userId);
        return itemRepository.getByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        throwIfUserNotExist(userId);
        return itemRepository.getById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: %s не найдена".formatted(itemId)));
    }

    @Override
    public ItemDto create(Long userId, ItemDto item) {
        User owner = throwIfUserNotExist(userId);
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(owner);
        newItem = itemRepository.create(newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto updateItem) {
        throwIfUserNotExist(userId);
        Item item = throwIfItemNotExist(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new ForbiddenException("Обновлять вещь может только ее владелец.");
        }
        Item updatedItem = itemRepository.update(item, updateItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        throwIfUserNotExist(userId);
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private User throwIfUserNotExist(Long id) {
        return userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }

    private Item throwIfItemNotExist(Long id) {
        return itemRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }
}
