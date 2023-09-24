package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();

    User get(int id);

    User create(UserDto dto);

    User update(Integer userId, UserDto dto);

    boolean delete(int id);
}