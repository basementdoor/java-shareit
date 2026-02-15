package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDtoTest() {
        User owner = User.builder()
                .id(100L)
                .name("owner")
                .email("o@example.com")
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("Drill")
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(20L)
                .name("booker")
                .email("b@example.com")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 1, 12, 0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());

        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getItem().getName()).isEqualTo("Drill");
        assertThat(dto.getItem().getOwnerId()).isEqualTo(100L);

        assertThat(dto.getBooker().getId()).isEqualTo(20L);
    }

    @Test
    void toBookingFromRequestBookingDtoTest() {
        RequestBookingDto req = RequestBookingDto.builder()
                .start(LocalDateTime.of(2024, 2, 1, 14, 0))
                .end(LocalDateTime.of(2024, 2, 1, 16, 0))
                .build();

        Booking booking = BookingMapper.toBookingFromRequest(req);

        assertThat(booking.getStart()).isEqualTo(req.getStart());
        assertThat(booking.getEnd()).isEqualTo(req.getEnd());
        assertThat(booking.getItem()).isNull();
        assertThat(booking.getBooker()).isNull();
    }

    @Test
    void toShortBookingDtoTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.of(2024, 3, 1, 9, 0))
                .end(LocalDateTime.of(2024, 3, 1, 11, 0))
                .build();

        ShortBookingDto dto = BookingMapper.toShortBookingDto(booking);

        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
    }
}
