package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemService itemService;
    private long itemId;
    private long userId;
    private Item expectedItem;
    private User user;
    private UserDto userDto;
    private User notOwner;
    private UserDto notOwnerDto;
    private Comment expectedComment;
    @Captor
    private ArgumentCaptor<Item> captor;

    @BeforeEach
    public void init() {
        userId = 1L;
        user = new User(userId, "test", "test@mail.ru");
        userDto = UserMapper.toUserDto(user);

        itemId = 1L;
        expectedItem = new Item(itemId, "tool", "cool tool", true, userId, null);

        notOwner = new User(2L, "fake", "fake@mail.ru");
        notOwnerDto = UserMapper.toUserDto(notOwner);

        expectedComment = Comment.builder()
            .id(1L)
            .text("cool")
            .item(expectedItem)
            .author(user)
            .created(LocalDateTime.now())
            .build();
    }

    @Test
    void saveItem_whenInvoked_thenItemReturned() {
        when(itemRepository.save(any())).thenReturn(expectedItem);

        ItemDto actual = itemService.save(userId, ItemMapper.toItemDto(expectedItem));

        assertEquals(expectedItem.getId(), actual.getId());
        assertEquals(expectedItem.getName(), actual.getName());
        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveItem_withRequest_thenItemReturned() {
        ItemRequest request = new ItemRequest(
            1L,
            "want this",
            notOwner,
            LocalDateTime.now()
        );

        expectedItem.setRequest(request);
        when(itemRepository.save(any())).thenReturn(expectedItem);

        ItemDto actual = itemService.save(userId, ItemMapper.toItemDto(expectedItem));

        assertEquals(expectedItem.getId(), actual.getId());
        assertEquals(expectedItem.getName(), actual.getName());
        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void findItemById_whenOwnerRequests_thenItemReturned() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ItemDto actual = itemService.findById(userId, itemId);

        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
    }

    @Test
    void findItemById_whenNotOwnerRequests_thenItemReturned() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ItemDto actual = itemService.findById(notOwner.getId(), itemId);

        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
    }

    @Test
    void saveComment_whenInvoked_thenCommentReturned() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        doNothing().when(bookingService).validateBookingsToAddComment(anyLong(), anyLong());
        when(commentRepository.save(any())).thenReturn(expectedComment);

        CommentDto actual = itemService.saveComment(1L, 1L, CommentMapper.toCommentDto(expectedComment));

        assertEquals(expectedComment.getId(), actual.getId());
        assertEquals(expectedComment.getCreated(), actual.getCreated());
        assertEquals(expectedComment.getText(), actual.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void findItemById_whenCommentsNotEmpty_thenItemReturned() {
        when(commentRepository.save(any())).thenReturn(expectedComment);
        List<Comment> comments = List.of(expectedComment);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        CommentDto commentDto = CommentMapper.toCommentDto(expectedComment);
        itemService.saveComment(notOwner.getId(), itemId, commentDto);
        ItemDto actual = itemService.findById(notOwner.getId(), itemId);

        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
        assertEquals(commentDto.getText(), actual.getComments().get(0).getText());
    }

    @Test
    void findItemById_whenItemNotFound_thenExceptionReturned() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(userId, itemId));
    }

    @Test
    void findItemById_whenUserNotFound_thenExceptionReturned() {
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(userId, itemId));
    }

    @Test
    void findItems_whenItemsFound_thenItemListReturned() {
        expectedItem.setOwner(null);
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of(expectedItem);
        when(itemRepository.findByOwner(1L, pageable)).thenReturn(items);

        List<Item> actualItems = itemService.findAll(userId, 0, 10)
            .stream()
            .map(ItemMapper::toItem)
            .collect(Collectors.toList());

        assertEquals(items, actualItems);
        assertEquals(1, actualItems.size());
        verify(itemRepository, times(1)).findByOwner(userId, pageable);
    }

    @Test
    void findItems_whenEmptyList_thenEmptyListReturned() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of();
        when(itemRepository.findByOwner(1L, pageable)).thenReturn(items);

        List<Item> actualItems = itemService.findAll(userId, 0, 10)
            .stream()
            .map(ItemMapper::toItem)
            .collect(Collectors.toList());

        assertEquals(items, actualItems);
        assertTrue(actualItems.isEmpty());
        verify(itemRepository, times(1)).findByOwner(userId, pageable);
    }

    @Test
    void search_whenItemsFound_thenItemListReturned() {
        expectedItem.setOwner(null);
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of(expectedItem);
        when(itemRepository.search("tool", pageable)).thenReturn(items);

        List<Item> actualItems = itemService.search(userId, "tool", 0, 10)
            .stream()
            .map(ItemMapper::toItem)
            .collect(Collectors.toList());

        assertEquals(items, actualItems);
        assertEquals(1, actualItems.size());
        verify(itemRepository, times(1)).search("tool", pageable);
    }

    @Test
    void search_whenTextIsNull_thenEmptyListReturned() {
        List<Item> items = new ArrayList<>();

        List<Item> actualItems = itemService.search(userId, null, 0, 10)
            .stream()
            .map(ItemMapper::toItem)
            .collect(Collectors.toList());

        assertEquals(items, actualItems);
        assertTrue(actualItems.isEmpty());
    }

    @Test
    void updateItem_whenOwnerRequests_thenItemReturned() {
        Item updatedItem = new Item();
        updatedItem.setName("Upd");
        updatedItem.setDescription("upd");
        updatedItem.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        itemService.save(userId, ItemMapper.toItemDto(expectedItem));
        itemService.update(userId, itemId, ItemMapper.toItemDto(updatedItem));

        verify(itemRepository, times(2)).save(captor.capture());
        Item savedItem = captor.getValue();

        assertEquals("Upd", savedItem.getName());
        assertEquals("upd", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateItem_whenNotOwnerRequests_thenExceptionReturned() {
        Item updatedItem = new Item();
        updatedItem.setName("Upd");
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        itemService.save(userId, ItemMapper.toItemDto(expectedItem));

        assertThrows(UserNotFoundException.class, () -> itemService.update(2L, itemId, ItemMapper.toItemDto(updatedItem)));
    }

    @Test
    void getItemsByRequestId_whenInvoked_thenReturnList() {
        ItemRequest request = new ItemRequest(
            1L,
            "want this",
            notOwner,
            LocalDateTime.now()
        );

        expectedItem.setRequest(request);
        List<ItemDtoInRequest> expected = List.of(ItemMapper.toItemDtoInRequest(expectedItem));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(expectedItem));

        List<ItemDtoInRequest> actual = itemService.getItemsByRequestId(1L);

        assertEquals(actual.size(), expected.size());
        assertEquals(actual.get(0).getDescription(), expected.get(0).getDescription());
        assertEquals(actual.get(0).getRequestId(), expected.get(0).getRequestId());
        assertEquals(actual.get(0).getName(), expected.get(0).getName());
        assertEquals(actual.get(0).getAvailable(), expected.get(0).getAvailable());
    }

    @Test
    void hasUserZeroItems_whenZero_thenReturnTrue() {
        List<Item> items = List.of();
        when(itemRepository.findAll()).thenReturn(items);
        boolean actual = itemService.hasUserZeroItems(2L);

        assertTrue(actual);
    }
}