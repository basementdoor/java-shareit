package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking currentBooking;
    private Booking futureRejectedBooking;
    private Booking pastWaitingBooking;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        booker = new User(null, "Booker", "booker@mail.com");

        item = new Item(null,
                "Item-1",
                "Desc",
                true,
                owner,
                null
        );

        LocalDateTime now = LocalDateTime.now();

        currentBooking = new Booking(
                null,
                now.minusDays(3),
                now.plusDays(2),
                item,
                booker,
                BookingStatus.APPROVED
        );

        futureRejectedBooking = new Booking(
                null,
                now.plusDays(3),
                now.plusDays(5),
                item,
                booker,
                BookingStatus.REJECTED
        );

        pastWaitingBooking = new Booking(
                null,
                now.minusDays(10),
                now.minusDays(8),
                item,
                booker,
                BookingStatus.WAITING
        );
    }

    @Test
    void createBookingSuccessTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        RequestBookingDto request = new RequestBookingDto(
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        BookingDto created = bookingService.create(booker.getId(), request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(created.getItem().getId()).isEqualTo(item.getId());
        assertThat(created.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void createBookingThrowIfItemUnavailableTest() {
        userRepository.save(owner);
        userRepository.save(booker);

        item.setAvailable(false);
        itemRepository.save(item);

        RequestBookingDto request = new RequestBookingDto(
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        assertThatThrownBy(() -> bookingService.create(booker.getId(), request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Предмет уже забронирован");
    }

    @Test
    void createBookingThrowIfItemNotFoundTest() {
        userRepository.save(booker);

        RequestBookingDto request = new RequestBookingDto(
                999L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        assertThatThrownBy(() -> bookingService.create(booker.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Предмет с ID: 999 не найден");
    }

    @Test
    void createBookingThrowIfUserNotFoundTest() {
        userRepository.save(owner);
        itemRepository.save(item);

        RequestBookingDto request = new RequestBookingDto(
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        assertThatThrownBy(() -> bookingService.create(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID: 999 не найден");
    }

    @Test
    void updateStatusApprovedTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        BookingDto updated = bookingService.updateStatus(owner.getId(), currentBooking.getId(), true);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void updateStatusRejectTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        BookingDto updated = bookingService.updateStatus(owner.getId(), currentBooking.getId(), false);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void updateStatusThrowIfNotOwnerTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        assertThatThrownBy(() -> bookingService.updateStatus(booker.getId(), currentBooking.getId(), true))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Изменить статус бронирования может только владелец предмета");
    }

    @Test
    void updateStatusThrowIfBookingNotFoundTest() {
        userRepository.save(owner);

        assertThatThrownBy(() -> bookingService.updateStatus(owner.getId(), 999L, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с номером: 999 не найдено");
    }

    @Test
    void getByIdSuccessTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        BookingDto byOwner = bookingService.getById(owner.getId(), currentBooking.getId());
        BookingDto byBooker = bookingService.getById(booker.getId(), currentBooking.getId());

        assertThat(byOwner.getId()).isEqualTo(currentBooking.getId());
        assertThat(byBooker.getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getByIdThrowIfNoAccessTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        User stranger = userRepository.save(new User(null, "str", "str@mail.com"));

        assertThatThrownBy(() -> bookingService.getById(stranger.getId(), currentBooking.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Запросить информацию о бронировании может только создатель брони или владелец предмета");
    }

    @Test
    void getByIdThrowIfBookingNotFoundTest() {
        userRepository.save(owner);

        assertThatThrownBy(() -> bookingService.getById(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с номером: 999 не найдено");
    }

    @Test
    void getAllByBookerReturnCorrectForAllStatesTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureRejectedBooking);
        bookingRepository.save(pastWaitingBooking);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.ALL)).hasSize(3);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.CURRENT)).hasSize(1);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE)).hasSize(1);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.PAST)).hasSize(1);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.WAITING)).hasSize(1);

        assertThat(bookingService.getAllByBooker(booker.getId(), BookingState.REJECTED)).hasSize(1);
    }

    @Test
    void getAllByBookerThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> bookingService.getAllByBooker(999L, BookingState.ALL))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllByOwnerReturnCorrectForAllStatesTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureRejectedBooking);
        bookingRepository.save(pastWaitingBooking);

        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.ALL)).hasSize(3);
        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT)).hasSize(1);
        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE)).hasSize(1);
        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.PAST)).hasSize(1);
        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.WAITING)).hasSize(1);
        assertThat(bookingService.getAllByOwner(owner.getId(), BookingState.REJECTED)).hasSize(1);
    }

    @Test
    void getAllByOwnerThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> bookingService.getAllByOwner(999L, BookingState.ALL))
                .isInstanceOf(NotFoundException.class);
    }
}

