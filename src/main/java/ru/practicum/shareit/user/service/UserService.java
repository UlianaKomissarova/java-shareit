package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto findById(Long id);

    UserDto save(UserDto dto);

    UserDto update(UserDto dto, Long userId);

    void delete(Long id);
}