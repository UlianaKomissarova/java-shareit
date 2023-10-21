package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemService itemService;
    private long itemId;
    private long userId;
    private Item expectedItem;
    private User user;
    private User notOwner;
    @Captor
    private ArgumentCaptor<Item> captor;

    @BeforeEach
    public void init() {
        userId = 1L;
        user = new User(userId, "test", "test@mail.ru");

        itemId = 1L;
        expectedItem = new Item(itemId, "tool", "cool tool", true, userId, null);

        notOwner = new User(2L, "fake", "fake@mail.ru");
    }

    @Test
    void saveItem_whenInvoked_thenItemReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        ItemDto actual = itemService.save(userId, ItemMapper.toItemDto(expectedItem));

        assertEquals(expectedItem.getId(), actual.getId());
        assertEquals(expectedItem.getName(), actual.getName());
        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
        verify(itemRepository).save(any(Item.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveComment_whenInvoked_thenCommentReturned() {
        Comment expectedComment = Comment.builder()
            .id(1L)
            .text("cool")
            .item(expectedItem)
            .author(user)
            .created(LocalDateTime.now())
            .build();

        List<Booking> bookings = List.of(new Booking(
            1L,
            LocalDateTime.of(2011, 11, 11, 11, 11),
            LocalDateTime.of(2012, 11, 11, 11, 11),
            expectedItem,
            user,
            null)
        );

        when(commentRepository.save(any())).thenReturn(expectedComment);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.findBookingsToAddComment(anyLong(), anyLong(), any())).thenReturn(bookings);

        CommentDto actual = itemService.saveComment(userId, itemId, CommentMapper.toCommentDto(expectedComment));

        assertEquals(expectedComment.getId(), actual.getId());
        assertEquals(expectedComment.getCreated(), actual.getCreated());
        assertEquals(expectedComment.getText(), actual.getText());
        verify(commentRepository).save(any(Comment.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveComment_whenOwner_thenExceptionReturned() {
        Comment expectedComment = Comment.builder()
            .id(1L)
            .text("cool")
            .item(expectedItem)
            .author(user)
            .created(LocalDateTime.now())
            .build();

        List<Booking> bookings = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.findBookingsToAddComment(anyLong(), anyLong(), any())).thenReturn(bookings);

        assertThrows(CommentBadRequestException.class,
            () -> itemService.saveComment(userId, itemId, CommentMapper.toCommentDto(expectedComment)));
    }

    @Test
    void findItemById_whenOwnerRequests_thenItemReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ItemDto actual = itemService.findById(userId, itemId);

        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
    }

    @Test
    void findItemById_whenNotOwnerRequests_thenItemReturned() {
        when(userRepository.findById(notOwner.getId())).thenReturn(Optional.of(notOwner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ItemDto actual = itemService.findById(notOwner.getId(), itemId);

        assertEquals(expectedItem.getDescription(), actual.getDescription());
        assertEquals(expectedItem.getAvailable(), actual.getAvailable());
    }

    @Test
    void findItemById_whenCommentsNotEmpty_thenItemReturned() {
        Comment expectedComment = Comment.builder()
            .id(1L)
            .text("cool")
            .item(expectedItem)
            .author(notOwner)
            .created(LocalDateTime.now())
            .build();

        List<Booking> bookings = List.of(new Booking(
            1L,
            LocalDateTime.of(2011, 11, 11, 11, 11),
            LocalDateTime.of(2012, 11, 11, 11, 11),
            expectedItem,
            notOwner,
            null)
        );

        when(commentRepository.save(any())).thenReturn(expectedComment);
        when(bookingRepository.findBookingsToAddComment(anyLong(), anyLong(), any())).thenReturn(bookings);
        List<Comment> comments = List.of(expectedComment);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(userRepository.findById(notOwner.getId())).thenReturn(Optional.of(notOwner));
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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(userId, itemId));
    }

    @Test
    void findItemById_whenUserNotFound_thenExceptionReturned() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.findById(userId, itemId));
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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        itemService.save(userId, ItemMapper.toItemDto(expectedItem));

        assertThrows(UserNotFoundException.class, () -> itemService.update(2L, itemId, ItemMapper.toItemDto(updatedItem)));
    }
}