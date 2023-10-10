package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
            .id(booking.getId())
            .start(booking.getStart())
            .end(booking.getEnd())
            .item(booking.getItem())
            .booker(booking.getBooker())
            .status(booking.getStatus())
            .build();
    }

    public static Booking toBooking(ShortBookingDto dto, Item item, User booker) {
        return Booking.builder()
            .start(dto.getStart())
            .end(dto.getEnd())
            .id(dto.getId())
            .booker(booker)
            .item(item)
            .build();
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return ShortBookingDto.builder()
            .id(booking.getId())
            .start(booking.getStart())
            .end(booking.getEnd())
            .itemId(booking.getItem().getId())
            .bookerId(booking.getBooker().getId())
            .build();
    }
}