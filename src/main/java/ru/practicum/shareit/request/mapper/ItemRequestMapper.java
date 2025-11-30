package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> answers) {
        ItemRequestDto requestDto = toItemRequestDto(itemRequest);
        List<ShortItemDto> shortAnswers = answers.stream()
                .map(ItemMapper::toShortItemDto)
                .toList();
        requestDto.setItems(shortAnswers);
        return requestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated())
                .build();
    }
}
