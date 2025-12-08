package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemServiceImpl itemService;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private User owner;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        user2 = userRepository.save(new User(null, "User2", "user2@mail.com"));

        item1 = itemRepository.save(
                new Item(null, "Item 1", "Description 1", true, owner, null)
        );

        item2 = itemRepository.save(
                new Item(null, "Item 2", "Description 2", true, owner, null)
        );
    }

    @Test
    void getAllByUserTest() {
        Long ownerId = owner.getId();
        Long itemId = item1.getId();
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED
        ));

        commentRepository.save(new Comment(
                null,
                "Test comment",
                item1,
                user2,
                LocalDateTime.now()
        ));

        Collection<ItemDtoWithBookingsAndComments> result = itemService.getAllByUser(ownerId);

        assertThat(result).hasSize(2);

        ItemDtoWithBookingsAndComments dto = result.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getLastBooking()).isNotNull();
    }

    @Test
    void getAllByUserReturnEmptyIfUserHasNoItemsTest() {
        User stranger = userRepository.save(new User(null, "Stranger", "s@gmail.com"));

        Collection<ItemDtoWithBookingsAndComments> result =
                itemService.getAllByUser(stranger.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllByUserThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> itemService.getAllByUser(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID");
    }

    @Test
    void getByIdReturnItemIfOwnerTest() {
        Long ownerId = owner.getId();
        Long itemId = item1.getId();

        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED
        ));

        ItemDtoWithBookingsAndComments dto = itemService.getById(ownerId, itemId);

        assertThat(dto).isNotNull();
        assertThat(dto.getLastBooking()).isNotNull();
    }

    @Test
    void getByIdHideBookingsWhenNotOwnerTest() {
        Long itemId = item1.getId();

        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED
        ));

        ItemDtoWithBookingsAndComments dto = itemService.getById(user2.getId(), itemId);

        assertThat(dto).isNotNull();
        assertThat(dto.getLastBooking()).isNull(); // НЕ владелец -> не видит брони
    }

    @Test
    void getByIdThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> itemService.getById(999L, item1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdThrowIfItemNotFoundTest() {
        assertThatThrownBy(() -> itemService.getById(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createItemWithoutItemRequestTest() {
        ItemDto dto = ItemDto.builder()
                .name("New")
                .description("Desc")
                .available(true)
                .build();

        ItemDto saved = itemService.create(owner.getId(), dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(dto.getName());
    }

    @Test
    void createSetRequestIfRequestExistsTest() {
        ItemRequest request = requestRepository.save(
                new ItemRequest(null, "Need item", user2, LocalDateTime.now())
        );

        ItemDto dto = ItemDto.builder()
                .name("Request")
                .description("Request description")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDto saved = itemService.create(owner.getId(), dto);

        assertThat(saved.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void createThrowIfUserNotFoundTest() {
        ItemDto dto = ItemDto.builder()
                .name("X")
                .description("Y")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.create(999L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createThrowIfRequestNotFoundTest() {
        ItemDto dto = ItemDto.builder()
                .name("X")
                .description("Y")
                .available(true)
                .requestId(999L)
                .build();

        assertThatThrownBy(() -> itemService.create(owner.getId(), dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateFieldsTest() {
        Long id = item1.getId();

        UpdateItemDto upd = new UpdateItemDto("New name", "New desc", false);

        ItemDto updated = itemService.update(owner.getId(), id, upd);

        assertThat(updated.getName()).isEqualTo("New name");
        assertThat(updated.getDescription()).isEqualTo("New desc");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateNotUpdateBlankFieldsTest() {
        Long id = item1.getId();
        UpdateItemDto upd = new UpdateItemDto(" ", " ", null);

        ItemDto updated = itemService.update(owner.getId(), id, upd);

        assertThat(updated.getName()).isEqualTo(item1.getName());
        assertThat(updated.getDescription()).isEqualTo(item1.getDescription());
    }

    @Test
    void updateThrowIfUserNotFoundTest() {
        UpdateItemDto upd = new UpdateItemDto("X", "Y", true);

        assertThatThrownBy(() -> itemService.update(999L, item1.getId(), upd))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateThrowIfItemNotFoundTest() {
        UpdateItemDto upd = new UpdateItemDto("X", "Y", true);

        assertThatThrownBy(() -> itemService.update(owner.getId(), 999L, upd))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateThrowIfNotOwnerTest() {
        UpdateItemDto upd = new UpdateItemDto("X", "Y", true);

        assertThatThrownBy(() -> itemService.update(user2.getId(), item1.getId(), upd))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("только ее владелец");
    }

    @Test
    void searchReturnItemsTest() {
        Collection<ItemDto> items =
                itemService.search("Description");

        assertThat(items).hasSize(2);
    }

    @Test
    void searchReturnEmptyIfBlankTest() {
        assertThat(itemService.search("")).isEmpty();
        assertThat(itemService.search("   ")).isEmpty();
        assertThat(itemService.search(null)).isEmpty();
    }

    @Test
    void addCommentIfUserHadBookingTest() {
        Long itemId = item1.getId();
        Long userId = user2.getId();

        // пользователь user2 завершил бронирование item1 в прошлом
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED
        ));

        CommentDto req = CommentDto.builder()
                .text("Comment")
                .build();

        CommentDto result = itemService.addComment(userId, itemId, req);

        assertThat(result.getText()).isEqualTo("Comment");
        assertThat(result.getAuthorName()).isEqualTo(user2.getName());
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void addCommentThrowIfUserNotBookedItemTest() {
        CommentDto req = CommentDto.builder()
                .text("Hello")
                .build();

        assertThatThrownBy(() -> itemService.addComment(user2.getId(), item1.getId(), req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не бронировал");
    }

    @Test
    void addCommentThrowIfUserNotFoundTest() {
        CommentDto req = CommentDto.builder()
                .text("X")
                .build();

        assertThatThrownBy(() -> itemService.addComment(999L, item1.getId(), req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentThrowIfItemNotFoundTest() {
        CommentDto req = CommentDto.builder()
                .text("X")
                .build();

        assertThatThrownBy(() -> itemService.addComment(user2.getId(), 999L, req))
                .isInstanceOf(NotFoundException.class);
    }
}

