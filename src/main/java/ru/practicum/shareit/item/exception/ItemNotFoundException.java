package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.core.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
