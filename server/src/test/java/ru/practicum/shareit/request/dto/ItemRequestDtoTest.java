package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serializeItemRequestDtoTest() throws Exception {
        var dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a Play Station")
                .requesterId(10L)
                .created(LocalDateTime.parse("2024-01-01T12:00:00"))
                .items(List.of(
                        ShortItemDto.builder()
                                .id(5L)
                                .name("Play Station")
                                .ownerId(10L)
                                .build()
                ))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.items[0].name");

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a Play Station");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(5);
    }

    @Test
    void deserializeItemRequestDtoTest() throws Exception {
        String content = """
                {
                  "id": 1,
                  "description": "Need a Play Station",
                  "requesterId": 10,
                  "created": "2024-01-01T12:00:00",
                  "items": [
                    { "id": 5, "name": "Play Station", "ownerId": 10 }
                  ]
                }
                """;

        var dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a Play Station");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().getFirst().getName()).isEqualTo("Play Station");
    }
}


