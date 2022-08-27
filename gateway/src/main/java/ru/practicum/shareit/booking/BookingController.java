package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForAdd;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingService;

    @Autowired
    public BookingController(BookingClient bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody BookingForAdd booking) {
        log.info("Add booking " + booking);
        return bookingService.bookItem(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> upDateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam String approved) {
        log.info("upDateStatus for bookingId " + bookingId);
        return bookingService.upDateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}") //есть
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        log.info("get bookingId " + bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping// есть
    public ResponseEntity<Object> getAllBookingForBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("get listBooking  for user " + bookerId);
        return bookingService.getAllBookingForBooker(bookerId,state);
    }

    @GetMapping("/owner")// есть
    public ResponseEntity<Object> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("get listBooking for owner " + ownerId);
        return bookingService.getAllBookingForOwner(ownerId,state);
    }
}
