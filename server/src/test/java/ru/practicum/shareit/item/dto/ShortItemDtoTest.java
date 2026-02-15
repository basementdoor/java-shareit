package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@JsonTest
class ShortItemDtoTest {

    @Autowired
    private JacksonTester<ShortItemDto> json;

    @Test
    void serializeShortItemDtoTest() throws Exception {
        var dto = ShortItemDto.builder()
                .id(1L)
                .name("Item")
                .ownerId(10L)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.ownerId");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
    }

    @Test
    void deserializeShortItemDtoTest() throws Exception {
        String content = """
                { "id": 1, "name": "Item", "ownerId": 10 }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwnerId()).isEqualTo(10L);
    }
}

