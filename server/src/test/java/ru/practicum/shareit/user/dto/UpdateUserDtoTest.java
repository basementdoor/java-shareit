package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserDtoTest {

    @Autowired
    private JacksonTester<UpdateUserDto> json;

    @Test
    void serializeUpdateUserDtoTest() throws Exception {
        var dto = UpdateUserDto.builder()
                .id(10L)
                .name("John")
                .email("john@example.com")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void deserializeUpdateUserDtoTest() throws Exception {
        String content = """
                {
                  "id": 2,
                  "name": "Kate",
                  "email": "kate@example.com"
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Kate");
        assertThat(dto.getEmail()).isEqualTo("kate@example.com");
    }
}

