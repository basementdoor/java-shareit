package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestBookingDtoTest {

    @Autowired
    private JacksonTester<RequestBookingDto> json;

    @Test
    void serializeRequestBookingDtoTest() throws Exception {
        var dto = RequestBookingDto.builder()
                .itemId(5L)
                .start(LocalDateTime.of(2024, 2, 1, 10, 0))
                .end(LocalDateTime.of(2024, 2, 1, 12, 0))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-02-01T10:00:00");
    }

    @Test
    void deserializeRequestBookingDtoTest() throws Exception {
        String jsonContent = """
                {
                  "itemId": 5,
                  "start": "2024-02-01T10:00:00",
                  "end": "2024-02-01T12:00:00"
                }
                """;

        var result = json.parse(jsonContent);

        assertThat(result.getObject().getItemId()).isEqualTo(5L);
        assertThat(result.getObject().getStart())
                .isEqualTo(LocalDateTime.of(2024, 2, 1, 10, 0));
    }
}

