package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    private long requestId;
    private long userId;
    private ItemRequest expectedRequest;
    private User requester;
    @Captor
    private ArgumentCaptor<ItemRequest> captor;

    @BeforeEach
    public void init() {
        userId = 1L;
        requester = new User(userId, "test", "test@mail.ru");

        requestId = 1L;
        expectedRequest = new ItemRequest(requestId, "good", requester, LocalDateTime.now());
    }

    @Test
    void findRequestById_whenRequestFound_thenRequestReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

        ItemRequestDto actual = requestService.findById(userId, requestId);

        assertEquals(expectedRequest.getDescription(), actual.getDescription());
        assertEquals(expectedRequest.getCreated(), actual.getCreated());
    }

    @Test
    void findRequestById_whenRequestNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> requestService.findById(userId, requestId));
    }

    @Test
    void findRequestById_whenUserNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> requestService.findById(userId, requestId));
    }

    @Test
    void saveRequest_whenInvoked_thenRequestReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.save(any())).thenReturn(expectedRequest);

        ItemRequestDto actual = requestService.save(userId, RequestMapper.toRequestDto(expectedRequest));

        assertEquals(expectedRequest.getId(), actual.getId());
        assertEquals(expectedRequest.getDescription(), actual.getDescription());
        verify(requestRepository).save(any(ItemRequest.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void findRequests_whenRequestsFound_thenRequestListReturned() {
        expectedRequest.setRequester(null);
        List<ItemRequest> requests = List.of(expectedRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findByRequesterId(1L, Sort.by("created").descending()))
            .thenReturn(requests);

        List<ItemRequest> actualRequests = requestService.findAll(userId)
            .stream()
            .map(RequestMapper::toRequest)
            .collect(Collectors.toList());

        assertEquals(requests, actualRequests);
        assertEquals(1, actualRequests.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1))
            .findByRequesterId(userId, Sort.by("created").descending());
    }

    @Test
    void findRequests_whenEmptyList_thenEmptyListReturned() {
        List<ItemRequest> requests = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findByRequesterId(1L, Sort.by("created").descending()))
            .thenReturn(requests);

        List<ItemRequest> actualRequests = requestService.findAll(userId)
            .stream()
            .map(RequestMapper::toRequest)
            .collect(Collectors.toList());

        assertEquals(requests, actualRequests);
        assertTrue(actualRequests.isEmpty());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1))
            .findByRequesterId(userId, Sort.by("created").descending());
    }

    @Test
    void findRequestsFromOtherUsers_whenRequestsFound_thenRequestListReturned() {
        expectedRequest.setRequester(null);
        List<ItemRequest> requests = List.of(expectedRequest);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findByRequesterIdIsNot(1L, pageable)).thenReturn(requests);

        List<ItemRequest> actualRequests = requestService.findAllFromOtherUsers(userId, 0, 10)
            .stream()
            .map(RequestMapper::toRequest)
            .collect(Collectors.toList());

        assertEquals(requests, actualRequests);
        assertEquals(1, actualRequests.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findByRequesterIdIsNot(userId, pageable);
    }
}