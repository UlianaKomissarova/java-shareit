package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.core.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}