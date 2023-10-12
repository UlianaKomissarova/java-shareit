package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.*;

import java.util.Collection;

public interface BookingService {
    BookingDto save(Long userId, ShortBookingDto dto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long id, Long userId);

    Collection<BookingDto> findByUserIdAndState(Long userId, String state, int from, int size);

    Collection<BookingDto> findBookingsByItemOwnerId(Long userId, String state, int from, int size);
}