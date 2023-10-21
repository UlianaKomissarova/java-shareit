package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceInterface;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceInterface itemServiceInterface;
    private static ItemDto itemDto;

    @Autowired
    public ItemControllerTest(ObjectMapper objectMapper, MockMvc mockMvc, ItemServiceInterface itemServiceInterface) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.itemServiceInterface = itemServiceInterface;
    }

    @BeforeAll
    static void init() {
        itemDto = ItemDto.builder()
            .name("tool")
            .description("cool tool")
            .available(true)
            .build();
    }

    @Test
    void saveItem_whenInvoked_thenStatusSuccessfulAndItemReturned() throws Exception {
        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemServiceInterface, times(1)).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveItem_whenEmptyName_thenThrowException() throws Exception {
        ItemDto itemDtoWithEmptyName = ItemDto.builder()
            .name("")
            .description("cool tool")
            .available(true)
            .build();

        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDtoWithEmptyName))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());

        verify(itemServiceInterface, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveItem_whenNameIsNull_thenThrowException() throws Exception {
        ItemDto itemDtoWithNullName = ItemDto.builder()
            .description("cool tool")
            .available(true)
            .build();

        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDtoWithNullName))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());

        verify(itemServiceInterface, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveItem_whenEmptyDescription_thenThrowException() throws Exception {
        ItemDto itemDtoWithEmptyDescription = ItemDto.builder()
            .name("tool")
            .description("")
            .available(true)
            .build();

        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDtoWithEmptyDescription))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());

        verify(itemServiceInterface, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveItem_whenDescriptionIsNull_thenThrowException() throws Exception {
        ItemDto itemDtoWithNullDescription = ItemDto.builder()
            .name("tool")
            .available(true)
            .build();

        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDtoWithNullDescription))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());

        verify(itemServiceInterface, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveItem_whenAvailableIsNull_thenThrowException() throws Exception {
        ItemDto itemDtoWithNullAvailable = ItemDto.builder()
            .name("tool")
            .description("cool tool")
            .build();

        when(itemServiceInterface.save(anyLong(), any(ItemDto.class))).thenThrow(UserBadRequestException.class);

        mockMvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDtoWithNullAvailable))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError());

        verify(itemServiceInterface, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void saveComment_whenInvoked_thenStatusSuccessfulAndCommentReturned() throws Exception {
        CommentDto commentDto = CommentDto.builder()
            .text("cool")
            .authorName("Luc")
            .created(LocalDateTime.now())
            .build();

        when(itemServiceInterface.saveComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(
                post("/items/{itemId}/comment", 1)
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(commentDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        verify(itemServiceInterface, times(1)).saveComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    public void findItem_whenExist_thenStatus200andItemReturned() throws Exception {
        when(itemServiceInterface.findById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(
                get("/items/{itemId}", 1)
                    .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemServiceInterface, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    public void findItem_whenNotExist_thenThrowNotFound() throws Exception {
        when(itemServiceInterface.findById(anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 1L)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findAll_whenInvoked_thenStatus200andReturnItemList() throws Exception {
        List<ItemDto> expectedItems = List.of(itemDto);
        when(itemServiceInterface.findAll(anyLong(), anyInt(), anyInt())).thenReturn(expectedItems);

        mockMvc.perform(
                get("/items")
                    .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        verify(itemServiceInterface, times(1)).findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void search_whenInvoked_thenStatus200andReturnItemList() throws Exception {
        List<ItemDto> expectedItems = List.of(itemDto);
        when(itemServiceInterface.search(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(expectedItems);

        mockMvc.perform(
                get("/items/search")
                    .header("X-Sharer-User-Id", 1)
                    .param("text", "t"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        verify(itemServiceInterface, times(1)).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    public void updateItem_thenStatus200andUpdatedReturns() throws Exception {
        when(itemServiceInterface.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(
                patch("/items/1")
                    .header("X-Sharer-User-Id", 1)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemServiceInterface, times(1)).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    public void updateItem_whenNotOwner_thenExceptionReturns() throws Exception {
        when(itemServiceInterface.update(anyLong(), anyLong(), any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                patch("/items/1")
                    .header("X-Sharer-User-Id", 2)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}