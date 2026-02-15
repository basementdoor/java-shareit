package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serializeItemDtoTest() throws Exception {
        var dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Desc")
                .available(true)
                .requestId(5L)
                .comments(List.of(
                        new CommentDto(10L, "Comment", "Author", LocalDateTime.parse("2024-01-01T12:00:00"))
                ))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.comments[0].text");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(10);
    }

    @Test
    void deserializeItemDtoTest() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Item",
                  "description": "Desc",
                  "available": true,
                  "requestId": 5,
                  "comments": [
                    {
                      "id": 10,
                      "text": "Comment",
                      "authorName": "Auth",
                      "created": "2024-01-01T12:00:00"
                    }
                  ]
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().iterator().next().getText()).isEqualTo("Comment");
    }
}

