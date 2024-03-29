package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        log.info("Add item " + item);
        return itemClient.add(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody ItemForUpdate item) {
        log.info("Update item " + itemId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long idUser,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get all items for user " + idUser);
        return itemClient.getAll(idUser, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                          @PathVariable long itemId) {
        log.info("Get item " + itemId);
        return itemClient.getItem(idUser, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemSearch(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get item with searched text " + text);
        return itemClient.getItemSearch(idUser, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto comments) {
        log.info("Add comment " + comments);
        return itemClient.addComment(userId, itemId, comments);
    }
}