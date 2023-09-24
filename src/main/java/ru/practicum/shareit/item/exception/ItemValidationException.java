package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.core.exception.ValidationException;

public class ItemValidationException extends ValidationException {
    public ItemValidationException(String message) {
        super(message);
    }
}
