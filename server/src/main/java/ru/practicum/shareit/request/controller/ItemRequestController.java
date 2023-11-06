package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceInterface;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceInterface requestService;

    @PostMapping
    public ItemRequestDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto dto) {
        return requestService.save(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findAll(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllFromOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return requestService.findAllFromOtherUsers(userId, from, size);
    }
}