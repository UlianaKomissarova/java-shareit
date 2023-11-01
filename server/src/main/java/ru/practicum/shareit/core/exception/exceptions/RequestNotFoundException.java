package ru.practicum.shareit.core.exception.exceptions;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}