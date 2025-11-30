package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ShortItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .ownerId(booking.getItem().getOwner().getId())
                        .build())
                .booker(ShortUserDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBookingFromRequest(RequestBookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return ShortBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
