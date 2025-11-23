package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long userId, RequestBookingDto booking);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getAllByBooker(Long userId, String state);

    Collection<BookingDto> getAllByOwner(Long userId, String state);
}
