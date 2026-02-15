package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serializeCommentDtoTest() throws Exception {
        var dto = new CommentDto(
                1L, "Hello", "User", LocalDateTime.parse("2024-01-01T10:00:00")
        );

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("User");
    }

    @Test
    void deserializeCommentDtoTest() throws Exception {
        String content = """
                {
                  "id": 1,
                  "text": "Hello",
                  "authorName": "User",
                  "created": "2024-01-01T10:00:00"
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Hello");
        assertThat(dto.getAuthorName()).isEqualTo("User");
    }
}

