package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ShortUserDtoTest {

    @Autowired
    private JacksonTester<ShortUserDto> json;

    @Test
    void serializeShortUserDtoTest() throws Exception {
        var dto = ShortUserDto.builder()
                .id(1L)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void deserializeShortUserDtoTest() throws Exception {
        String content = """
                {
                  "id": 5
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(5L);
    }
}


