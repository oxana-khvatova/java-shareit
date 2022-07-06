package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public List<ItemDto> toItemDtoList(List<Item> items){
        List<ItemDto> listDto = new ArrayList<ItemDto>();
        if(items.size()==0) {
            return listDto;
        }
        for (Item item: items){
            ItemDto dto = toItemDto(item);
            listDto.add(dto);
        }
        return listDto;
    }
}