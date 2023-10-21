package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceInterface;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceInterface itemServiceInterface;

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto dto) {
        return itemServiceInterface.save(userId, dto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId,
        @Valid @RequestBody CommentDto dto) {
        return itemServiceInterface.saveComment(userId, itemId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId,
        @RequestBody ItemDto dto) {
        return itemServiceInterface.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemServiceInterface.findById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemServiceInterface.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam String text,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemServiceInterface.search(userId, text, from, size);
    }
}