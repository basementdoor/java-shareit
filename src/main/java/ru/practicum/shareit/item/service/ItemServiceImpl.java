package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Collections;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAllByUser(Long userId) {
        throwIfUserNotExist(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        throwIfUserNotExist(userId);
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Предмет с ID: %s не найдена".formatted(itemId)));
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto item) {
        User owner = throwIfUserNotExist(userId);
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(owner);
        newItem = itemRepository.save(newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, UpdateItemDto updateItem) {
        throwIfUserNotExist(userId);
        Item item = throwIfItemNotExist(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new ForbiddenException("Обновлять вещь может только ее владелец.");
        }
        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            item.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            item.setDescription(updateItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private User throwIfUserNotExist(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }

    private Item throwIfItemNotExist(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с ID: %s не найден".formatted(id)));
    }
}
