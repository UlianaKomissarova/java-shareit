package ru.practicum.shareit.core.exception;

public abstract class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {
        super(message);
    }
}
