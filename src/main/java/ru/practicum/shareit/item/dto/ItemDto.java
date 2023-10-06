package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private List<CommentDto> comments;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
}