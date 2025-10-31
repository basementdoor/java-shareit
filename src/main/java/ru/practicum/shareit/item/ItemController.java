package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @RequestBody @Valid ItemDto item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable @Positive Long itemId,
                          @RequestBody @Valid UpdateItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

}
