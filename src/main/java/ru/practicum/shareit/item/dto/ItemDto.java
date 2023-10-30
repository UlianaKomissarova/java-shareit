package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private List<CommentDto> comments;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Long requestId;
}