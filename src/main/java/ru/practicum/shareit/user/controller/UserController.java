package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return userService.get(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable int userId, @Valid @RequestBody UserDto dto) {
        return userService.update(userId, dto);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) {
        return userService.delete(id);
    }
}