package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid ItemRequestDto request) {
        log.info("Создать запрос на вещь {} от пользователя userId={}", request, userId);
        return requestClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получить запросы от пользователя userId={}", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получить все запросы, пользователь userId={}", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @Positive @PathVariable Long requestId) {
        log.info("Получить запрос на вещь requestId={} от пользователя userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
