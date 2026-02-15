package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDtoTest() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(10L)
                .description("Need a pencil")
                .created(now)
                .build();

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Need a pencil");

        assertThat(dto.getRequesterId()).isEqualTo(10L);

        assertThat(dto.getCreated()).isEqualTo(now);
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void toItemRequestDtoWithItemsTest() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Need tools")
                .created(now)
                .build();

        User owner = new User();
        owner.setId(10L);

        Item item1 = Item.builder()
                .id(2L)
                .name("Hammer")
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(3L)
                .name("Saw")
                .owner(owner)
                .build();

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, List.of(item1, item2));

        assertThat(dto.getItems()).hasSize(2);

        assertThat(dto.getItems().get(0).getId()).isEqualTo(2L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Hammer");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(10L);

        assertThat(dto.getItems().get(1).getId()).isEqualTo(3L);
        assertThat(dto.getItems().get(1).getName()).isEqualTo("Saw");
        assertThat(dto.getItems().get(1).getOwnerId()).isEqualTo(10L);
    }


    @Test
    void toItemRequestDtoWithEmptyItemsTest() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Need a TV")
                .created(now)
                .build();

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, List.of());

        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toItemRequestTest() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(5L)
                .description("Test desc")
                .created(now)
                .build();

        ItemRequest request = ItemRequestMapper.toItemRequest(dto);

        assertThat(request.getId()).isEqualTo(5L);
        assertThat(request.getDescription()).isEqualTo("Test desc");
        assertThat(request.getCreated()).isEqualTo(now);
    }

    @Test
    void toItemRequestCreatedNowIfNullTest() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(7L)
                .description("Some request")
                .created(null)
                .build();

        ItemRequest request = ItemRequestMapper.toItemRequest(dto);

        assertThat(request.getId()).isEqualTo(7L);
        assertThat(request.getDescription()).isEqualTo("Some request");

        assertThat(request.getCreated()).isNotNull();
    }
}

