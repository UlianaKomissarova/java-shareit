package ru.practicum.shareit.core.exception.exceptions;

public abstract class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
