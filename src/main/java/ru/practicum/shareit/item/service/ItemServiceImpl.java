package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toShortBookingDto;
import static ru.practicum.shareit.item.dto.CommentMapper.*;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto save(Long userId, ItemDto dto) {
        getExistingUser(userId);

        Item item = toItem(dto);
        item.setOwner(userId);
        setRequestWhenCreateItem(item, dto);
        item = itemRepository.save(item);

        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        Item item = getExistingItem(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new UserNotFoundException("Id пользователя не совпадает.");
        }

        updateItemProperties(item, dto);
        item = itemRepository.save(item);

        return fillItemWithCommentsAndBookings(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto findById(Long userId, Long itemId) {
        getExistingUser(userId);
        Item item = getExistingItem(itemId);
        ItemDto result = toItemDto(item);
        fillComments(result, itemId);

        if (item.getOwner().equals(userId)) {
            fillBookings(result);
            return result;
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> findAll(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDto> result = new ArrayList<>();
        List<Item> items = itemRepository.findByOwner(userId, pageable);

        for (Item item : items) {
            result.add(fillItemWithCommentsAndBookings(item));
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> search(Long userId, String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDto> result = new ArrayList<>();
        List<Item> foundItems = itemRepository.search(text, pageable);

        for (Item foundItem : foundItems) {
            result.add(fillItemWithCommentsAndBookings(foundItem));
        }

        return result;
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long userId, Long itemId, CommentDto dto) {
        User user = getExistingUser(userId);
        Item item = getExistingItem(itemId);

        List<Booking> previousBookings = bookingRepository.findBookingsToAddComment(itemId, userId, LocalDateTime.now());
        validateBookingForComment(previousBookings);

        Comment comment = toComment(dto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(user);

        return toCommentDto(commentRepository.save(comment));
    }

    private void validateBookingForComment(List<Booking> previousBookings) {
        if (previousBookings.isEmpty()) {
            throw new CommentBadRequestException(
                "Пользователь может оставить комментарий только на вещь, которую ранее использовал."
            );
        }
        for (Booking booking : previousBookings) {
            if (booking.getEnd().isAfter(LocalDateTime.now())) {
                throw new CommentBadRequestException("Оставить комментарий можно только после окончания срока аренды");
            }
        }
    }

    private User getExistingUser(long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException("Пользователь с id " + id + " не найден.")
        );
    }

    private Item getExistingItem(long id) {
        return itemRepository.findById(id).orElseThrow(
            () -> new ItemNotFoundException("Товар с id " + id + " не найден.")
        );
    }

    private ItemRequest getExistingRequest(long id) {
        return requestRepository.findById(id).orElseThrow(
            () -> new RequestNotFoundException("Запрос с id " + id + " не найден.")
        );
    }

    private void setRequestWhenCreateItem(Item item, ItemDto dto) {
        if (dto.getRequestId() != null) {
            Long requestId = dto.getRequestId();
            ItemRequest request = getExistingRequest(requestId);
            item.setRequest(request);
        }
    }

    private void updateItemProperties(Item item, ItemDto dto) {
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
    }

    private ItemDto fillItemWithCommentsAndBookings(Item item) {
        ItemDto result = toItemDto(item);
        fillComments(result, item.getId());
        fillBookings(result);

        return result;
    }

    private void fillComments(ItemDto result, Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (!comments.isEmpty()) {
            result.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        } else {
            result.setComments(new ArrayList<>());
        }
    }

    private void fillBookings(ItemDto result) {
        LocalDateTime now = LocalDateTime.now();
        bookingRepository
            .findBookingByItemIdAndStartBefore(result.getId(), now)
            .stream()
            .findFirst().ifPresent(lastBooking -> result.setLastBooking(toShortBookingDto(lastBooking)));

        bookingRepository
            .findBookingByItemIdAndStartAfter(result.getId(), now)
            .stream()
            .findFirst().ifPresent(nextBooking -> result.setNextBooking(toShortBookingDto(nextBooking)));

        if (result.getLastBooking() == null) {
            result.setNextBooking(null);
        }
    }
}