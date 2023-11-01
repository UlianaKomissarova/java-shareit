package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody RequestDto dto) {
        log.info("Creating request {}, userId {}", dto, userId);
        return requestClient.save(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Get request with id {}", requestId);
        return requestClient.findById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all requests, userId {}", userId);
        return requestClient.findAll(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllFromOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get all requests from other user {}", userId);
        return requestClient.findAllFromOtherUsers(userId, from, size);
    }
}