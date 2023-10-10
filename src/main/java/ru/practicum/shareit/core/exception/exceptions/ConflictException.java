package ru.practicum.shareit.core.exception.exceptions;

public abstract class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}