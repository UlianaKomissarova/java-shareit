package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.service.BookingServiceInterface;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceInterface bookingServiceInterface;

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return bookingServiceInterface.findBookingsByItemOwnerId(userId, state, from, size);
    }

    @GetMapping
    public Collection<BookingDto> findByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return bookingServiceInterface.findByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingServiceInterface.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ShortBookingDto dto) {
        return bookingServiceInterface.save(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId,
        @RequestParam Boolean approved
    ) {
        return bookingServiceInterface.approve(userId, bookingId, approved);
    }
}