package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.List;

@RestController
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
                          @Valid @RequestBody Booking booking) {
        Booking bookingSave = bookingService.save(booking, userId);
        return bookingMapper.toBookingDto(bookingSave);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto upDateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam String approved) {
        Booking booking = bookingService.upDateStatus(userId, bookingId, approved);
        return bookingMapper.toBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        Booking booking = bookingService.getBooking(userId, bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllBookingForBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String params) {
        List<Booking> booking = bookingService.getAllBookingForUser(bookerId);
        return bookingMapper.toBookingDtoList(booking);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String params) {
        List<Booking> booking = bookingService.getAllBookingForOwner(ownerId);
        return bookingMapper.toBookingDtoList(booking);
    }
}
