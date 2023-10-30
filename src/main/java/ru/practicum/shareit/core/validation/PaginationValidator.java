package ru.practicum.shareit.core.validation;

import ru.practicum.shareit.core.exception.exceptions.PaginationBadRequestException;

public class PaginationValidator {
    public static void validatePagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new PaginationBadRequestException("Параметры пагинации должны быть положительными.");
        }
    }
}
