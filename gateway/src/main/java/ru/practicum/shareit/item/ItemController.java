package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @Valid @RequestBody ItemRequestDto dto) {
        log.info("Creating item {}, userId {}", dto, userId);
        return itemClient.saveItem(userId, dto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> save(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId,
        @Valid @RequestBody CommentRequestDto dto) {
        log.info("Creating comment {}, userId {}, itemId {}", dto, userId, itemId);
        return itemClient.saveComment(userId, itemId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId,
        @RequestBody ItemRequestDto dto) {
        log.info("Updating item with id {}", itemId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Get item with id {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get all items, userId {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam String text,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Searching items including text {}", text);
        return itemClient.search(userId, text, from, size);
    }
}