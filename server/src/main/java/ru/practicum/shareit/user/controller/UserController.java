package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceInterface;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceInterface userServiceInterface;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userServiceInterface.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userServiceInterface.findById(id);
    }

    @PostMapping
    public UserDto save(@RequestBody UserDto dto) {
        return userServiceInterface.save(dto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto dto, @PathVariable Long userId) {
        return userServiceInterface.update(dto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userServiceInterface.delete(id);
    }
}