package ru.practicum.shareit.requests;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getUserRequesterId(),
                itemRequest.getCreated()
        );
    }

    public List<ItemRequestDto> toItemRequestDto(List<ItemRequest> itemRequest) {
        List<ItemRequestDto> list = new ArrayList<>();
        for (ItemRequest ir : itemRequest) {
            list.add(toItemRequestDto(ir));
        }
        return list;
    }
}