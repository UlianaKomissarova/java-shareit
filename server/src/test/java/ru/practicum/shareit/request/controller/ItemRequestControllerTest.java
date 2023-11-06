package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceInterface;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestServiceInterface requestService;
    private static ItemRequestDto requestDto;
    private static ItemRequestDto requestWithNullDescription;
    private static ItemRequestDto requestWithBlankDescription;

    @Autowired
    public ItemRequestControllerTest(ObjectMapper objectMapper, MockMvc mockMvc, ItemRequestServiceInterface requestService) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.requestService = requestService;
    }

    @BeforeAll
    static void init() {
        requestDto = ItemRequestDto.builder()
            .description("wow")
            .created(LocalDateTime.now())
            .build();

        requestWithNullDescription = ItemRequestDto.builder()
            .created(LocalDateTime.now())
            .build();

        requestWithBlankDescription = ItemRequestDto.builder()
            .description("")
            .created(LocalDateTime.now())
            .build();
    }

    @Test
    void saveRequest_whenInvoked_thenStatusSuccessfulAndRequestReturned() throws Exception {
        when(requestService.save(anyLong(), any(ItemRequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(
                post("/requests")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        verify(requestService, times(1)).save(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void saveRequest_whenNoHeader_thenExceptionReturned() throws Exception {
        when(requestService.save(anyLong(), any(ItemRequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(
                post("/requests")
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is5xxServerError());

        verify(requestService, never()).save(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void findRequest_whenExist_thenStatus200andRequestReturned() throws Exception {
        when(requestService.findById(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(
                get("/requests/{requestId}", 1)
                    .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        verify(requestService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    void findRequest_whenNotExist_thenThrowNotFound() throws Exception {
        when(requestService.findById(anyLong(), anyLong())).thenThrow(RequestNotFoundException.class);

        mockMvc.perform(
                get("/requests/{requestId}", 1)
                    .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    void findAll_whenInvoked_thenStatus200andReturnedRequestList() throws Exception {
        List<ItemRequestDto> expectedRequests = List.of(requestDto);
        when(requestService.findAll(anyLong())).thenReturn(expectedRequests);

        mockMvc.perform(
                get("/requests")
                    .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));

        verify(requestService, times(1)).findAll(anyLong());
    }

    @Test
    void findAllFromOtherUsers_whenInvoked_thenStatus200andReturnedRequestList() throws Exception {
        List<ItemRequestDto> expectedRequests = List.of(requestDto);
        when(requestService.findAllFromOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(expectedRequests);

        mockMvc.perform(
                get("/requests/all")
                    .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));

        verify(requestService, times(1)).findAllFromOtherUsers(anyLong(), anyInt(), anyInt());
    }
}