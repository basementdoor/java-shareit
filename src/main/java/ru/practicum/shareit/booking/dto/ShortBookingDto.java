package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortBookingDto {
    @FutureOrPresent
    LocalDateTime start;
    LocalDateTime end;
}
