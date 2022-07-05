package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        Item itemFromModel = booking.getItem();
        String name = itemFromModel.getName();
        String description = itemFromModel.getDescription();
        Boolean available = itemFromModel.getAvailable();
        BookingDto.Item dtoItem = new BookingDto.Item(name, description, available);
        return new BookingDto(booking.getStart(), booking.getEnd(), dtoItem, booking.getStatus());
    }
}