package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDtoIfNoRequestTest() {
        Item item = Item.builder()
                .id(1L)
                .name("Item-1")
                .description("Desc")
                .isAvailable(true)
                .request(null)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void toItemDtoIfRequestTest() {
        ItemRequest req = new ItemRequest(5L, null, null, null);

        Item item = Item.builder()
                .id(1L)
                .name("Item-1")
                .description("Desc")
                .isAvailable(true)
                .request(req)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void toShortItemDtoTest() {
        User owner = User.builder().id(10L).name("John").email("j@e.com").build();

        Item item = Item.builder()
                .id(2L)
                .name("Phone")
                .owner(owner)
                .build();

        ShortItemDto dto = ItemMapper.toShortItemDto(item);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Phone");
        assertThat(dto.getOwnerId()).isEqualTo(10L);
    }

    @Test
    void toItemTest() {
        ItemDto dto = ItemDto.builder()
                .id(3L)
                .name("Table")
                .description("Wood")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertThat(item.getId()).isEqualTo(3L);
        assertThat(item.getName()).isEqualTo("Table");
        assertThat(item.getDescription()).isEqualTo("Wood");
        assertThat(item.isAvailable()).isTrue();
    }

    @Test
    void toItemDtoWithBookingsNoBookingsTest() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("D")
                .isAvailable(true)
                .build();

        ItemDtoWithBookingsAndComments dto =
                ItemMapper.toItemDtoWithBookingsAndComments(item, List.of(), List.of());

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void toItemDtoWithBookingsOnlyLastBookingTest() {
        Item item = Item.builder().id(1L).build();

        Booking last = Booking.builder()
                .id(10L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        ItemDtoWithBookingsAndComments dto =
                ItemMapper.toItemDtoWithBookingsAndComments(item, List.of(last), List.of());

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getLastBooking().getEnd()).isEqualTo(last.getEnd());
    }

    @Test
    void toItemDtoWithBookingsOnlyNextBookingTest() {
        Item item = Item.builder().id(1L).build();

        Booking next = Booking.builder()
                .id(11L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ItemDtoWithBookingsAndComments dto =
                ItemMapper.toItemDtoWithBookingsAndComments(item, List.of(next), List.of());

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getStart()).isEqualTo(next.getStart());
    }

    @Test
    void toItemDtoWithBookingsTest() {
        Item item = Item.builder().id(1L).build();

        Booking last = Booking.builder()
                .id(10L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();

        Booking next = Booking.builder()
                .id(20L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ItemDtoWithBookingsAndComments dto =
                ItemMapper.toItemDtoWithBookingsAndComments(item, List.of(last, next), List.of());

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getLastBooking().getEnd()).isEqualTo(last.getEnd());
        assertThat(dto.getNextBooking().getStart()).isEqualTo(next.getStart());
    }

    @Test
    void toItemDtoWithBookingsCommentsTest() {
        Item item = Item.builder().id(1L).build();

        User author = User.builder().id(7L).name("Bob").email("b@e.com").build();

        Comment comment = Comment.builder()
                .id(100L)
                .text("Nice!")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        ItemDtoWithBookingsAndComments dto =
                ItemMapper.toItemDtoWithBookingsAndComments(item, List.of(), List.of(comment));

        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().iterator().next().getText()).isEqualTo("Nice!");
        assertThat(dto.getComments().iterator().next().getAuthorName()).isEqualTo("Bob");
    }
}

