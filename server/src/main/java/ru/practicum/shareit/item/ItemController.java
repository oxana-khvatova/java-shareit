package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapperForOwner;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.ItemForUpdate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemMapperForOwner itemMapperForOwner;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper,
                          CommentMapper commentMapper, ItemMapperForOwner itemMapperForOwner) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.itemMapperForOwner = itemMapperForOwner;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        Item itemSave = itemService.save(itemDto, userId);
        log.info("Add item " + itemDto);
        return itemMapper.toItemDto(itemSave);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @Valid @RequestBody ItemForUpdate item) {
        log.info("udDate item " + item);
        return itemService.upDate(item, userId, itemId);
    }

    @GetMapping
    public List<ItemOwnerDto> getAll(@RequestHeader("X-Sharer-User-Id") long idUser,
                                     @RequestParam(required = false, defaultValue = "0") int from,
                                     @RequestParam(required = false, defaultValue = "20") int size) {
        List<Item> allUserItems = new ArrayList<>(itemService.findAllOwnerItems(idUser, from, size));
        log.info("У пользователя в базе: {}", allUserItems.size() + " предметов");
        return itemMapperForOwner.toItemDtoList(allUserItems, idUser);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                @PathVariable long itemId) {
        Item item = itemService.findById(itemId);
        return itemMapperForOwner.toItemOwnerDto(item, idUser);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestHeader("X-Sharer-User-Id") long idUser,
                                       @RequestParam(required = false) String text,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "20") int size) {
        List<Item> list = itemService.search(text, from, size);
        return itemMapper.toItemDtoList(list);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody Comments comments) {
        Comments commentsSave = itemService.addComments(userId, itemId, comments);
        return commentMapper.toCommentDto(commentsSave);
    }
}