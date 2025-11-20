package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

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
}
