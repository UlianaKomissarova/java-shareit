package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private long userId;
    private User expectedUser;
    @Captor
    private ArgumentCaptor<User> captor;

    @BeforeEach
    public void init() {
        userId = 1L;
        expectedUser = new User(userId, "test", "test@mail.ru");
    }

    @Test
    void getUserById_whenUserFound_thenUserReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actual = userService.findById(userId);

        assertEquals(expectedUser.getId(), actual.getId());
        assertEquals(expectedUser.getName(), actual.getName());
        assertEquals(expectedUser.getEmail(), actual.getEmail());
    }

    @Test
    void getUserById_whenUserNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void saveUser_whenUserEmailValid_thenUserReturned() {
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actual = userService.save(UserMapper.toUserDto(expectedUser));

        assertEquals(expectedUser.getId(), actual.getId());
        assertEquals(expectedUser.getName(), actual.getName());
        assertEquals(expectedUser.getEmail(), actual.getEmail());
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUser_whenUserFound_thenUserReturned() {
        User updatedUser = new User();
        updatedUser.setName("Upd");
        updatedUser.setEmail("upd@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        userService.update(UserMapper.toUserDto(updatedUser), userId);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("Upd", savedUser.getName());
        assertEquals("upd@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenNameAndEmailAreBlank_thenUserReturned() {
        User updatedUser = new User();
        updatedUser.setName("");
        updatedUser.setEmail("");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        userService.update(UserMapper.toUserDto(updatedUser), userId);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test", savedUser.getName());
        assertEquals("test@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenNameAndEmailAreNull_thenUserReturned() {
        User updatedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        userService.update(UserMapper.toUserDto(updatedUser), userId);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test", savedUser.getName());
        assertEquals("test@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(UserMapper.toUserDto(expectedUser), userId));
    }

    @Test
    void findUsers_whenUsersFound_thenUserListReturned() {
        List<User> users = List.of(expectedUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.findAll().stream().map(UserMapper::toUser).collect(Collectors.toList());

        assertEquals(users, actualUsers);
        assertEquals(1, actualUsers.size());
    }

    @Test
    void findUsers_whenEmptyList_thenEmptyListReturned() {
        List<User> users = List.of();
        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.findAll().stream().map(UserMapper::toUser).collect(Collectors.toList());

        assertEquals(users, actualUsers);
        assertTrue(actualUsers.isEmpty());
    }

    @Test
    void deleteUser_whenUserFound_thenNothingReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    }
}