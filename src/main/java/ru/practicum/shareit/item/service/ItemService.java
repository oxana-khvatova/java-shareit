package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@Service
public class ItemService {

    ItemStorage itemStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ArrayList<Item> search(String name, long idOwner) {
        ArrayList<Item> listItem = new ArrayList<>();
        if (name.equals("")) {
            return listItem;
        }
        for (Item item : itemStorage.findAllItems()) {
            String text = name.toLowerCase();
            if (item.getName().toLowerCase().contains(text) && item.getAvailable()) {
                listItem.add(item);
            }
            if (item.getDescription().toLowerCase().contains(text) && !listItem.contains(item)
                    && item.getAvailable()) {
                listItem.add(item);
            }
        }
        return listItem;
    }
}
