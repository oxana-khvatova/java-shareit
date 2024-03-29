package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    private final CommentMapper commentMapper;

    @Autowired
    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                item.getOwner() != null ? item.getOwner() : null,
                commentMapper.toCommentListDto(item.getComments())
        );
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> listDto = new ArrayList<>();
        if (items.size() == 0) {
            return listDto;
        }
        for (Item item : items) {
            ItemDto dto = toItemDto(item);
            listDto.add(dto);
        }
        return listDto;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        if (itemDto.getIdOwner() != null) {
            item.setOwner(itemDto.getIdOwner());
        }
        if (itemDto.getIdItemRequest() != null) {
            item.setRequest(itemDto.getIdItemRequest());
        }
        if (itemDto.getComments() != null) {
            List<Comments> comments = commentMapper.toCommentsList(itemDto.getComments());
            item.setComments(comments);
        }
        return item;
    }
}