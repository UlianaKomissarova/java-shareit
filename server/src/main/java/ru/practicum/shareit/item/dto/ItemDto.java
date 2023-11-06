package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

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
    private List<CommentDto> comments;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Long requestId;
}