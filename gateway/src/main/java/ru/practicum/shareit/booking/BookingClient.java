package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingForAdd;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllBookingForOwner(long userId, String state) {
        Map<String, Object> parameters = Map.of(
                "status", state
        );
        return get("/" + userId, userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookingForAdd requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingForBooker(long userId, String state) {
        Map<String, Object> parameters = Map.of(
                "status", state
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> upDateStatus(long userId, Long bookingId, String approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId, userId, parameters);
    }

    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long idUser,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("",idUser, parameters);
    }
}
