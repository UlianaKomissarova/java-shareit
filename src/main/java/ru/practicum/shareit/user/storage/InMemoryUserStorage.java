package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int userId = 0;

    public static Integer increaseUserId() {
        return InMemoryUserStorage.userId += 1;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User get(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }

        return users.get(id);
    }

    @Override
    public User create(UserDto dto) {
        validateEmailAlreadyExist(dto);

        Integer id = increaseUserId();
        User user = UserMapper.toUser(id, dto);
        users.put(id, user);

        return user;
    }

    @Override
    public User update(Integer id, UserDto dto) {
        User user = get(id);
        updateUserData(user, dto);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public boolean delete(int id) {
        get(id);
        users.remove(id);

        return true;
    }

    private void validateEmailAlreadyExist(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new UserValidationException(
                "Электронный адрес должен присутствовать при создании пользователя."
            );
        }

        for (User user : users.values()) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new UserAlreadyExistException(
                    "Пользователь с электронным адресом " + userDto.getEmail() + " уже существует."
                );
            }
        }
    }

    private void updateUserData(User user, UserDto dto) {
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        for (User value : users.values()) {
            if (value.getEmail().equals(dto.getEmail()) && !value.getId().equals(user.getId())) {
                throw new UserAlreadyExistException(
                    "Пользователь с электронным адресом " + dto.getEmail() + " уже существует."
                );
            }
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }
}