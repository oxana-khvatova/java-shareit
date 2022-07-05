package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForUpdate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    InMemoryItemStorage inMemoryItemStorage;

    @Autowired
    public ItemController(InMemoryItemStorage inMemoryItemStorage) {
        this.inMemoryItemStorage = inMemoryItemStorage;
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody Item item) {
        inMemoryItemStorage.add(userId, item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId,
                       @Valid @RequestBody ItemForUpdate item) {
        return inMemoryItemStorage.update(userId, itemId, item);
    }

    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") long idUser) {
        List<Item> allUserItems = new ArrayList<>(inMemoryItemStorage.findAll(idUser));
        log.info("У пользователя в базе: {}", allUserItems.size());
        return allUserItems;
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                        @PathVariable long itemId) {
        return inMemoryItemStorage.findById(itemId);
    }

    @GetMapping("/search")
    public ArrayList<Item> getItemSearch(@RequestHeader("X-Sharer-User-Id") long idUser,
                                         @RequestParam(required = false) String text) {
        return inMemoryItemStorage.search(text, idUser);
    }
}