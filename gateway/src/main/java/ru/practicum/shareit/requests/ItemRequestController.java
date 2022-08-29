package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestsClient requestClient;

    @Autowired
    public ItemRequestController(RequestsClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemRequestDto request) {
        log.info("Create itemRequest " + request);
        return requestClient.add(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get itemRequest for user " + userId);
        return requestClient.getAllUserRequest(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get all itemRequests for user " + userId);
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable @Valid Long requestId) {
        log.info("Get itemRequest " + requestId);
        return requestClient.getRequest(requestId, userId);
    }
}