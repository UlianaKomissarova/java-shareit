package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.core.exception.AlreadyExistException;

public class UserAlreadyExistException extends AlreadyExistException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}