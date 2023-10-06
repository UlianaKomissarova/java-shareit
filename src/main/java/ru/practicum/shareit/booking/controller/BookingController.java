package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findBookingsByItemOwnerId(userId, state);
    }

    @GetMapping
    public Collection<BookingDto> findByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findByUserIdAndState(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ShortBookingDto dto) {
        return bookingService.save(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId,
        @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }
}