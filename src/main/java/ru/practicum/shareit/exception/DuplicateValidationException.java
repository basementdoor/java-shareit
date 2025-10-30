package ru.practicum.shareit.exception;

public class DuplicateValidationException extends RuntimeException {
    public DuplicateValidationException(String message) {
        super(message);
    }
}
