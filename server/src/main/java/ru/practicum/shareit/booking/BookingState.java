package ru.practicum.shareit.booking;

import lombok.Getter;

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
}
