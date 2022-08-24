package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequest request) {
        log.info("Add item request " + request);
        return requestService.save(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all request for user id = " + userId);
        return requestService.getAllUserRequest(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(required = false, defaultValue = "0") int from,
                                              @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get all request");
        return requestService.findAllRequest(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable @Valid Long requestId) {
        log.info("Get request id = " + requestId);
        return requestService.getRequest(requestId);
    }
}