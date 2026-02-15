package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBookingsAndComments {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    ShortBookingDto lastBooking;
    ShortBookingDto nextBooking;
    Collection<CommentDto> comments;
}
