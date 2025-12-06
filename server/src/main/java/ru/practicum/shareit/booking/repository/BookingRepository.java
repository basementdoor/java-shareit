package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            AND b.start <= CURRENT_TIMESTAMP
            AND b.end >= CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    Collection<Booking> findAllCurrentBookingsByBookerId(Long bookerId);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateAfterEnd);

    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateBeforeStart);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.start <= CURRENT_TIMESTAMP
            AND b.end >= CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    Collection<Booking> findAllCurrentBookingsByItemOwnerId(Long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id IN :itemIds
            AND b.status = 'APPROVED'
            AND (b.end < CURRENT_TIMESTAMP OR b.start > CURRENT_TIMESTAMP)
            ORDER BY b.item.id, b.start
            """)
    Collection<Booking> findAllBookingsForItems(List<Long> itemIds);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.item
            JOIN FETCH b.booker
            WHERE b.item.id = :itemId
            AND b.booker.id = :userId
            AND b.status = 'APPROVED'
            AND b.end < CURRENT_TIMESTAMP
            """)
    Collection<Booking> findPastBookingsForUserByItemId(Long userId, Long itemId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status = 'APPROVED'
            """)
    List<Booking> findAllByItemId(Long itemId);
}
