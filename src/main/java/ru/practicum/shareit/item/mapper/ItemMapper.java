package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .build();
    }

    public ItemDtoWithBookingsAndComments toItemDtoWithBookingsAndComments(Item item,
                                                                           List<Booking> bookings,
                                                                           Collection<Comment> comments) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (bookings != null && !bookings.isEmpty()) {

            lastBooking = bookings.stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

        return ItemDtoWithBookingsAndComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .lastBooking(lastBooking == null ? null : BookingMapper.toShortBookingDto(lastBooking))
                .nextBooking(nextBooking == null ? null : BookingMapper.toShortBookingDto(nextBooking))
                .comments(comments.stream().map(CommentMapper::toCommentDto).toList())
                .build();
    }
}
