package ru.practicum.shareit.core.exception.exceptions;

public abstract class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}