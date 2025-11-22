package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBookings {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    Long requestId;
    ShortBookingDto lastBooking;
    ShortBookingDto nextBooking;
}
