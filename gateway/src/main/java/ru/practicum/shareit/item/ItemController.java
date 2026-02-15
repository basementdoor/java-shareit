package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос всех вещей пользователя с userId={}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Positive @PathVariable Long itemId) {
        log.info("Запрос вещи с itemId={}, пользователь userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid ItemDto item) {
        log.info("Создание вещи item={}, пользователь userId={}", item, userId);
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody @Valid UpdateItemDto item) {
        log.info("Обновление вещи с itemId={}, item={} пользователь userId={}", itemId, item, userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text) {
        log.info("Поиск вещи с текстом={}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody @Valid CommentDto commentRequest) {
        log.info("Добавить комментарий {} от пользователя id={} к вещи itemId={}", commentRequest, authorId, itemId);
        return itemClient.addComment(authorId, itemId, commentRequest);
    }
}
