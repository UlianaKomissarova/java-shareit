package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.*;
import static ru.practicum.shareit.booking.model.Status.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    public static final Sort SORT = Sort.by("start").descending();

    @Transactional
    @Override
    public BookingDto save(Long userId, ShortBookingDto dto) {
        Item item = getExistingItem(dto.getItemId());
        User booker = getExistingUser(userId);

        if (item.getOwner().equals(userId)) {
            throw new BookingNotFoundException("Вещь не может быть забронирована ее владельцем.");
        }

        if (!item.getAvailable()) {
            throw new BookingBadRequestException("В данный момент товар недоступен для бронирования.");
        }

        validateEndAndStart(dto);

        Booking booking = toBooking(dto, item, booker);
        booking.setStatus(WAITING);

        return toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getExistingBooking(bookingId);
        Item item = getExistingItem(booking.getItem().getId());

        if (!item.getOwner().equals(userId)) {
            throw new BookingNotFoundException("Запрос может быть выполнен только владельцем вещи.");
        }

        Status status = approved ? APPROVED : REJECTED;
        if (booking.getStatus().equals(status)) {
            throw new BookingBadRequestException("Ваша заявка уже ожидает подтверждения.");
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findById(Long id, Long userId) {
        getExistingUser(userId);
        Booking booking = getExistingBooking(id);
        validateRequestor(booking, userId);

        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findByUserIdAndState(Long userId, String state) {
        getExistingUser(userId);

        state = checkUserBookingState(state);
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, SORT);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, WAITING, SORT);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, SORT);
                break;
            default :
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findBookingsByItemOwnerId(Long userId, String state) {
        getExistingUser(userId);
        hasUserZeroItems(userId);

        state = checkUserBookingState(state);
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingsByItemOwner(userId, SORT);
                break;
            case "CURRENT":
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findBookingsByItemOwnerAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingsByItemOwnerAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingsByItemOwnerAndStatus(userId, WAITING, SORT);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingsByItemOwnerAndStatus(userId, REJECTED, SORT);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
    }

    private void validateEndAndStart(ShortBookingDto dto) {
        if (dto.getStart() == null) {
            throw new BookingBadRequestException("Срок начала аренды не указан.");
        }

        if (dto.getEnd() == null) {
            throw new BookingBadRequestException("Срок конца аренды не указан.");
        }

        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new BookingBadRequestException("Конец срока аренды не может быть раньше старта.");
        }

        if (dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("Конец срока аренды должен быть позже.");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("Старт срока аренды должен быть раньше.");
        }

        if (dto.getEnd().isEqual(dto.getStart())) {
            throw new BookingBadRequestException("Конец срока аренды не может совпадать со стартом.");
        }
    }

    private String checkUserBookingState(String state) {
        if (state == null || state.isBlank()) {
            state = "ALL";
        }

        return state;
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

    private Booking getExistingBooking(long id) {
        return bookingRepository.findById(id).orElseThrow(
            () -> new BookingNotFoundException("Бронирование с id " + id + " не найдено.")
        );
    }

    private void hasUserZeroItems(long userId) {
        boolean hasZeroItems = itemRepository.findAll()
            .stream()
            .filter(item -> item.getOwner().equals(userId))
            .findAny().isEmpty();

        if (hasZeroItems) {
            throw new BookingBadRequestException("Этот запрос имеет смысл для владельца хотя бы одной вещи. ");
        }
    }

    private void validateRequestor(Booking booking, long userId) {
        long bookingAuthorId = booking.getBooker().getId();
        long itemOwnerId = booking.getItem().getOwner();

        if (bookingAuthorId != userId && itemOwnerId != userId) {
            throw new BookingNotFoundException("Запрос может быть выполнен либо автором бронирования, " +
                "либо владельцем вещи, к которой относится бронирование.");
        }
    }
}