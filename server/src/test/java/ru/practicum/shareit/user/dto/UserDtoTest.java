package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serializeUserDtoTest() throws Exception {
        var dto = UserDto.builder()
                .id(3L)
                .name("Mike")
                .email("mike@example.com")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Mike");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mike@example.com");
    }

    @Test
    void deserializeUserDtoTest() throws Exception {
        String content = """
                {
                  "id": 4,
                  "name": "Sara",
                  "email": "sara@example.com"
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getName()).isEqualTo("Sara");
        assertThat(dto.getEmail()).isEqualTo("sara@example.com");
    }
}

