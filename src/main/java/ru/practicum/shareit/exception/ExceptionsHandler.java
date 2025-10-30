package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException exception) {
        log.warn("Не найдена сущность для запроса {}", exception.getMessage());
        return new ErrorResponse("Не найдено", exception.getMessage());
    }

    @ExceptionHandler(DuplicateValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateError(DuplicateValidationException exception) {
        log.error("Конфликт данных {}", exception.getMessage());
        return new ErrorResponse("Дупликация информации:", exception.getMessage());
    }

    public record ErrorResponse(String error, String details) {}
}
