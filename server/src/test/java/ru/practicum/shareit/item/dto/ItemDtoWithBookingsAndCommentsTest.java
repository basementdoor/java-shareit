package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingsAndCommentsTest {

    @Autowired
    private JacksonTester<ItemDtoWithBookingsAndComments> json;

    @Test
    void serializeItemDtoWithBookingsTest() throws Exception {
        var dto = ItemDtoWithBookingsAndComments.builder()
                .id(1L)
                .name("Item")
                .description("Desc")
                .available(true)
                .requestId(5L)
                .lastBooking(ShortBookingDto.builder()
                                .start(LocalDateTime.parse("2024-01-01T12:00:00"))
                                .end(LocalDateTime.parse("2024-01-02T12:00:00"))
                                .build())
                .nextBooking(ShortBookingDto.builder()
                        .start(LocalDateTime.parse("2024-02-01T12:00:00"))
                        .end(LocalDateTime.parse("2024-02-02T12:00:00"))
                        .build())
                .comments(List.of(new CommentDto(1L, "Hi", "User", null)))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.lastBooking.start");
        assertThat(result).hasJsonPath("$.comments[0].text");
    }

    @Test
    void deserializeItemDtoWithBookingsTest() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Item",
                  "description": "Desc",
                  "available": true,
                  "requestId": 5,
                  "lastBooking": {
                      "start": "2024-01-01T12:00:00",
                      "end": "2024-01-02T12:00:00"
                  },
                  "nextBooking": {
                      "start": "2024-02-01T12:00:00",
                      "end": "2024-02-02T12:00:00"
                  },
                  "comments": [
                    { "id": 1, "text": "Hi", "authorName": "User" }
                  ]
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getComments()).hasSize(1);
    }
}

