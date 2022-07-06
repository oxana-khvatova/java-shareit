package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForUpdate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    ItemStorage inMemoryItemStorage;
    ItemService itemService;
    ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemStorage inMemoryItemStorage, ItemService itemService, ItemMapper itemMapper) {
        this.inMemoryItemStorage = inMemoryItemStorage;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }
    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody Item item) {
        inMemoryItemStorage.add(userId, item);
        return itemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId,
                       @Valid @RequestBody ItemForUpdate item) {
        Item itemUpdate = inMemoryItemStorage.update(userId, itemId, item);
        return itemMapper.toItemDto(itemUpdate);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long idUser) {
        List<Item> allUserItems = new ArrayList<>(inMemoryItemStorage.findAllOwnerItems(idUser));
        log.info("У пользователя в базе: {}", allUserItems.size());
        return itemMapper.toItemDtoList(allUserItems);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                        @PathVariable long itemId) {
        Item item = inMemoryItemStorage.findById(itemId);
        return itemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestHeader("X-Sharer-User-Id") long idUser,
                                         @RequestParam(required = false) String text) {
        ArrayList<Item> list = itemService.search(text, idUser);
        return itemMapper.toItemDtoList(list);
    }
}