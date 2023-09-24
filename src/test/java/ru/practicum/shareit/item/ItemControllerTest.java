package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void add() throws Exception {
        ItemDto dto = new ItemDto("test", "test", true, null);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void addWithoutHeader() throws Exception {
        ItemDto dto = new ItemDto("test", "test", true, null);

        mockMvc.perform(post("/items")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void get() throws Exception {
        final int itemId = 1;
        Item item = new Item(itemId, "cool", "cool", true, 1, null);
        given(itemService.get(1, itemId)).willReturn(item);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.name").value(item.getName()));
    }

    @Test
    void getAll() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "cool", "cool", true, 1, null));
        items.add(new Item(2, "second", "second", true, 2, null));
        given(itemService.getAll(1)).willReturn(items);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/items")
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().is2xxSuccessful())
            .andDo(print());
    }

    @Test
    void update() throws Exception {
        final int itemId = 1;
        ItemDto dto = new ItemDto("test", "test", true, null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1, itemId, dto)
                .header("X-Sharer-User-Id", 1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void search() throws Exception {
        String text = "super";

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                .header("X-Sharer-User-Id", 1)
                .contentType("application/json")
                .param("text", text))
            .andExpect(status().is2xxSuccessful());
    }
}
