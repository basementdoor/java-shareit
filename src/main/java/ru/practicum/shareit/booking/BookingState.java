package ru.practicum.shareit.booking;

import lombok.Getter;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Arrays;

@Getter
public enum BookingState {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String value;

    BookingState(String value) {
        this.value = value;
    }

    public static BookingState from(String text) {
        return Arrays.stream(values())
                .filter(s -> s.value.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Неизвестный статус бронирования: " + text));
    }
}
