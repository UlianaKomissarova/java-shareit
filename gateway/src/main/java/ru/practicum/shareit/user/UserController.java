package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@Positive @PathVariable Long id) {
        log.info("Get user with id " + id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserRequestDto dto) {
        log.info("Creating user {}", dto);
        return userClient.save(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserRequestDto dto, @Positive @PathVariable Long userId) {
        log.info("Updating user with id " + userId);
        return userClient.update(dto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Positive @PathVariable Long id) {
        log.info("Deleting user with id " + id);
        return userClient.delete(id);
    }
}