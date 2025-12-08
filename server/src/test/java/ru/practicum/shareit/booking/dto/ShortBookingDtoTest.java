package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ShortBookingDtoTest {

    @Autowired
    private JacksonTester<ShortBookingDto> json;

    @Test
    void serializeShortBookingDtoTest() throws Exception {
        var dto = ShortBookingDto.builder()
                .start(LocalDateTime.of(2024, 3, 10, 10, 0))
                .end(LocalDateTime.of(2024, 3, 10, 11, 0))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-03-10T10:00:00");
    }

    @Test
    void deserializeShortBookingDtoTest() throws Exception {
        String jsonContent = """
                {
                  "start": "2024-03-10T10:00:00",
                  "end": "2024-03-10T11:00:00"
                }
                """;

        var parsed = json.parse(jsonContent);

        ShortBookingDto dto = parsed.getObject();

        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 3, 10, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 3, 10, 11, 0));
    }
}

