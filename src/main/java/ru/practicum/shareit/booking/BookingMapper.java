package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForAdd;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingMapper(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    public BookingDto toBookingDto(Booking booking) {
        Item itemFromModel = itemService.findById(booking.getItemId());
        User user = userService.findById(booking.getBookerId());
        BookingDto.Booker booker = new BookingDto.Booker(user.getId());
        BookingDto.Item dtoItem = new BookingDto.Item(itemFromModel.getId(), itemFromModel.getName(),
                itemFromModel.getDescription(), itemFromModel.getAvailable());
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), dtoItem,
                booking.getStatus(), booker);
    }

    public List<BookingDto> toBookingDtoList(List<Booking> bookingList) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(toBookingDto(booking));
        }
        return bookingDtoList;
    }

    public Booking toBooking(BookingForAdd bookingForAdd) {
        Booking booking = new Booking();
        LocalDateTime start = bookingForAdd.getStart() == null ? null : bookingForAdd.getStart();
        booking.setStart(start);
        LocalDateTime end = bookingForAdd.getEnd() == null ? null : bookingForAdd.getEnd();
        booking.setEnd(end);
        Long itemId = bookingForAdd.getItemId();
        booking.setItemId(itemId);
        Status status = bookingForAdd.getStatus();
        booking.setStatus(status);
        return booking;
    }
}