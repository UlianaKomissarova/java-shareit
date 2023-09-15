package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User get(int id) {
        return userStorage.get(id);
    }

    @Override
    public User create(UserDto dto) {
        return userStorage.create(dto);
    }

    @Override
    public User update(Integer userId, UserDto dto) {
        return userStorage.update(userId, dto);
    }

    @Override
    public boolean delete(int id) {
        return userStorage.delete(id);
    }
}