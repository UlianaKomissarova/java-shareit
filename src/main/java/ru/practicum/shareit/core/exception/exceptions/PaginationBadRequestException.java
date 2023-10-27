package ru.practicum.shareit.core.exception.exceptions;

public class PaginationBadRequestException extends BadRequestException {
    public PaginationBadRequestException(String message) {
        super(message);
    }
}