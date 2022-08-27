package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingForAdd {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Long itemId;
    private Long bookerId;
    private Status status;
}
