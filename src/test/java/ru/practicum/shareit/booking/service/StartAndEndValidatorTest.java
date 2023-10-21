package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.core.exception.exceptions.BookingBadRequestException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StartAndEndValidatorTest {
    @InjectMocks
    private StartAndEndValidator validator;

    @Test
    void validate_whenEndEqualStart_thenExceptionReturned() {
        ShortBookingDto bookingWithEndEqualStart = new ShortBookingDto(
            2L,
            LocalDateTime.of(2026, 11, 11, 11, 11),
            LocalDateTime.of(2026, 11, 11, 11, 11),
            1L,
            2L
        );

        assertThrows(BookingBadRequestException.class,
            () -> validator.validate(bookingWithEndEqualStart));
    }

    @Test
    void validate_whenEndBeforeStart_thenExceptionReturned() {
        ShortBookingDto bookingWithEndBeforeStart = new ShortBookingDto(
            3L,
            LocalDateTime.of(2026, 11, 11, 11, 11),
            LocalDateTime.of(2026, 10, 11, 11, 11),
            1L,
            2L
        );

        assertThrows(BookingBadRequestException.class,
            () -> validator.validate(bookingWithEndBeforeStart));
    }
}