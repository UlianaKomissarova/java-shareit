package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private static UserDto userDto;
    private static UserDto userWithNullEmail;
    private static UserDto userWithIncorrectEmail;

    @Autowired
    public UserControllerTest(ObjectMapper objectMapper, MockMvc mockMvc, UserService userService) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    @BeforeAll
    static void init() {
        userDto = UserDto.builder()
            .name("user")
            .email("test@mail.ru")
            .build();

        userWithNullEmail = UserDto.builder()
            .name("null email")
            .build();

        userWithIncorrectEmail = UserDto.builder()
            .name("incorrect email")
            .email("mail.ru")
            .build();
    }

    @Test
    void saveUser_whenInvoked_thenStatusSuccessfulAndUserReturned() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(
                post("/users")
                    .content(objectMapper.writeValueAsString(userDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    void saveUser_whenIncorrectEmail_thenThrowException() throws Exception {
        when(userService.save(any(UserDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/users")
                    .content(objectMapper.writeValueAsString(userWithIncorrectEmail))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());
    }

    @Test
    void saveUser_whenEmailIsNull_thenThrowException() throws Exception {
        when(userService.save(any(UserDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/users")
                    .content(objectMapper.writeValueAsString(userWithNullEmail))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void findUser_whenExist_thenStatus200andUserReturned() throws Exception {
        when(userService.findById(anyLong())).thenReturn(userDto);

        mockMvc.perform(
                get("/users/{userId}", 1))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    public void findUser_whenNotExist_thenThrowNotFound() throws Exception {
        when(userService.findById(anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findAll_whenInvoked_thenStatus200andReturnUserList() throws Exception {
        List<UserDto> expectedUsers = List.of(userDto);
        Mockito.when(userService.findAll()).thenReturn(expectedUsers);

        mockMvc.perform(
                get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));

        verify(userService, times(1)).findAll();
    }

    @Test
    public void updateUser_thenStatus200andUpdatedReturns() throws Exception {
        when(userService.update(any(), anyLong())).thenReturn(userDto);

        mockMvc.perform(
                patch("/users/1")
                    .content(objectMapper.writeValueAsString(userDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(userDto.getName()));

        verify(userService, times(1)).update(any(UserDto.class), anyLong());
    }

    @Test
    public void deleteUser_whenInvoked_thenStatus200() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(
                delete("/users/1"))
            .andExpect(status().isOk());
    }
}