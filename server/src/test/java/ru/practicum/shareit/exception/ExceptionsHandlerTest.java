package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsHandlerTest {

    private final ExceptionsHandler handler = new ExceptionsHandler();

    @Test
    void handleNotFoundError_shouldReturnErrorResponse() {
        NotFoundException exception = new NotFoundException("Сущность не найдена");

        ExceptionsHandler.ErrorResponse response = handler.handleNotFoundError(exception);

        assertThat(response.error()).isEqualTo("Не найдено");
        assertThat(response.details()).isEqualTo("Сущность не найдена");
    }

    @Test
    void handleDuplicateError_shouldReturnErrorResponse() {
        DuplicateValidationException exception = new DuplicateValidationException("Конфликт данных");

        ExceptionsHandler.ErrorResponse response = handler.handleDuplicateError(exception);

        assertThat(response.error()).isEqualTo("Дупликация информации:");
        assertThat(response.details()).isEqualTo("Конфликт данных");
    }

    @Test
    void handleForbiddenError_shouldReturnErrorResponse() {
        ForbiddenException exception = new ForbiddenException("Нет прав");

        ExceptionsHandler.ErrorResponse response = handler.handleForbiddenError(exception);

        assertThat(response.error()).isEqualTo("Доступ запрещен:");
        assertThat(response.details()).isEqualTo("Нет прав");
    }

    @Test
    void handleValidationError_shouldReturnErrorResponse() {
        ValidationException exception = new ValidationException("Некорректные данные");

        ExceptionsHandler.ErrorResponse response = handler.handleValidationError(exception);

        assertThat(response.error()).isEqualTo("Ошибка запроса: ");
        assertThat(response.details()).isEqualTo("Некорректные данные");
    }
}
