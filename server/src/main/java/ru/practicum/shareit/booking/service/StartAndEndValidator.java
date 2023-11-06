package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.core.exception.exceptions.BookingBadRequestException;

@Component
public class StartAndEndValidator {
    public void validate(ShortBookingDto dto) throws BookingBadRequestException {
        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new BookingBadRequestException("Конец срока аренды не может быть раньше старта.");
        }

        if (dto.getEnd().isEqual(dto.getStart())) {
            throw new BookingBadRequestException("Конец срока аренды не может совпадать со стартом.");
        }
    }
}