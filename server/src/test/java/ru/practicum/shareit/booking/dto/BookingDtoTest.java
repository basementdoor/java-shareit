package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeBookingDtoTest() throws Exception {
        ShortItemDto item = ShortItemDto.builder()
                .id(1L)
                .name("Item-1")
                .build();

        ShortUserDto user = ShortUserDto.builder()
                .id(2L)
                .build();

        BookingDto dto = BookingDto.builder()
                .id(10L)
                .start(LocalDateTime.of(2024, 1, 1, 12, 0))
                .end(LocalDateTime.of(2024, 1, 2, 12, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item-1");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
    }

    @Test
    void deserializeBookingDtoTest() throws Exception {
        String jsonContent = """
                {
                  "id": 10,
                  "start": "2024-01-01T12:00:00",
                  "end": "2024-01-02T12:00:00",
                  "item": { "id": 1, "name": "Item-1" },
                  "booker": { "id": 2 },
                  "status": "APPROVED"
                }
                """;

        var parsed = json.parse(jsonContent);

        BookingDto dto = parsed.getObject();

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(dto.getItem().getName()).isEqualTo("Item-1");
    }
}

