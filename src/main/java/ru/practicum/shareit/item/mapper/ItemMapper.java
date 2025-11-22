package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static ru.practicum.shareit.util.Constants.NOW;

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

    public ItemDtoWithBookings toItemDtoWithBookings(Item item, List<Booking> bookings) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (bookings != null && !bookings.isEmpty()) {

            lastBooking = bookings.stream()
                    .filter(b -> b.getEnd().isBefore(NOW))
                    .max((b1, b2) -> b1.getEnd().compareTo(b2.getEnd()))
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(NOW))
                    .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                    .orElse(null);
        }

        return ItemDtoWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .lastBooking(lastBooking == null ? null : BookingMapper.toShortBookingDto(lastBooking))
                .nextBooking(nextBooking == null ? null : BookingMapper.toShortBookingDto(nextBooking))
                .build();
    }
}
