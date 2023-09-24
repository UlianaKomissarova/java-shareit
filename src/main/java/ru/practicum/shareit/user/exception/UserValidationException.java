package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.core.exception.ValidationException;

public class UserValidationException extends ValidationException {
    public UserValidationException(String message) {
        super(message);
    }
}
