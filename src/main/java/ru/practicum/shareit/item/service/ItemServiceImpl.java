package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Collection<ItemDtoWithBookingsAndComments> getAllByUser(Long userId) {
        throwIfUserNotExist(userId);
        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemsIds = items.stream().map(Item::getId).toList();
        Collection<Booking> bookings = bookingRepository.findAllBookingsForItems(itemsIds);

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Collection<Comment> comments = commentRepository.findAllByItemIds(itemsIds);
        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> ItemMapper.toItemDtoWithBookingsAndComments(item,
                        bookingsByItem.getOrDefault(item.getId(), List.of()),
                        commentsByItem.getOrDefault(item.getId(), List.of())))
                .toList();
    }

    @Override
    public ItemDtoWithBookingsAndComments getById(Long userId, Long itemId) {
        throwIfUserNotExist(userId);
        Item item = throwIfItemNotExist(itemId);
        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        // если юзер не является собственников, не передаем информацию о бронированиях
        List<Booking> bookings = item.getOwner().getId().equals(userId)
                ? bookingRepository.findAllByItemId(itemId)
                : List.of();
        return itemRepository.findById(itemId)
                .map(i -> ItemMapper.toItemDtoWithBookingsAndComments(i, bookings, comments))
                .orElseThrow(() -> new NotFoundException("Предмет с ID: %s не найден".formatted(itemId)));
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

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentRequest) {
        Item item = throwIfItemNotExist(itemId);
        User author = throwIfUserNotExist(authorId);
        Collection<Booking> pastUserBookings = bookingRepository.findPastBookingsForUserByItemId(authorId, itemId);
        if (pastUserBookings.isEmpty()) {
            throw new ValidationException("Пользователь не бронировал предмет с ID: %s".formatted(itemId));
        }
        Comment comment = CommentMapper.toComment(commentRequest);
        comment.setItem(item);
        comment.setAuthor(author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
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
