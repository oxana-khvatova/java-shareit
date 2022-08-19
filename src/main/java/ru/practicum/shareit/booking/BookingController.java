package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingForAdd;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;
    BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody BookingForAdd booking) {
        log.info("Add booking " + booking);
        return bookingService.save(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto upDateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam String approved) {
        log.info("upDateStatus for bookingId " + bookingId);
        return bookingService.upDateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        log.info("get bookingId " + bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingForBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("get listBooking  for user " + bookerId);
        return bookingService.getAllBookingForUser(bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("get listBooking for owner " + ownerId);
        return bookingService.getAllBookingForOwner(ownerId);
    }
}