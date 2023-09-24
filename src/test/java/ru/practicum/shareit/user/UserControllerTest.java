package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        UserDto dto = new UserDto("test", "test@galaxy.net");

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void createBadEmail() throws Exception {
        UserDto dto = new UserDto("test", "test");

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void get() throws Exception {
        final int userId = 1;
        User user = new User(userId, "cool", "cool@mail.ru");
        given(userService.get(userId)).willReturn(user);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.name").value(user.getName()))
            .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void getAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "first", "first@mail.ru"));
        users.add(new User(2, "second", "second@mail.ru"));
        users.add(new User(3, "third", "third@mail.ru"));
        given(userService.getAll()).willReturn(users);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
            .andExpect(status().is2xxSuccessful())
            .andDo(print());
    }

    @Test
    void delete() throws Exception {
        final int userId = 1;
        User user = new User(userId, "cool", "cool@mail.ru");
        given(userService.get(userId)).willReturn(user);
        given(userService.delete(userId)).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
            .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        final int userId = 1;
        UserDto upd = new UserDto("update", "update@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId, upd)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(upd)))
            .andExpect(status().is2xxSuccessful())
            .andDo(print());
    }
}
