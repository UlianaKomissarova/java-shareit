package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User get(int id);

    User create(UserDto dto);

    User update(Integer id, UserDto dto);

    boolean delete(int id);
}