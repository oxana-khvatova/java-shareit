package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForUpdate;
import ru.practicum.shareit.user.InMemoryUserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;
    private static long idGenerator = 1;

    @Autowired
    public InMemoryItemStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public Item add(Long userId, Item item) {
        if (item.getId() != null) {
            log.error("Can't add item: validation failed id != 0");
            throw new ValidationException();
        }
        if (!items.containsKey(item.getId())) {
            if (userId != null) {
                item.setOwner(inMemoryUserStorage.findById(userId));
            } else {
                throw new UserNotFoundException("Can't find owner with id " + userId);
            }
            item.setId(idGenerator++);
            items.put(item.getId(), item);
            log.info("Add item" + item);
            return item;
        } else {
            log.error("User already exist");
            throw new ItemNotFoundException("Item " + item + " not found ");
        }
    }

    @Override
    public Item update(long userId, long itemId, ItemForUpdate newItem) {
        if (itemId == 0) {
            log.error("Can't update item: validation failed id == 0");
            throw new ValidationException();
        }
        Item item = items.get(itemId);
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenAccessException("Updating an item is only possible for owner");
        }
        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        items.replace(item.getId(), item);
        log.info("Updated item " + item);
        return item;
    }

    @Override
    public void delete(Item item) {
        if (item.getId() == 0) {
            log.error("Can't delete item: validation failed id != 0");
            throw new ValidationException();
        }
        if (items.containsKey(item.getId())) {
            items.remove(item.getId());
        } else {
            throw new UserNotFoundException("Item " + item + " not found");
        }
    }

    @Override
    public List<Item> findAll(long idOwner) {
        return items.values().stream().filter(item -> idOwner == (item.getOwner().getId())).collect(Collectors.toList());
    }

    @Override
    public Item findById(Long id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Item id= " + id + " not found");
        }
        return items.get(id);
    }

    public ArrayList<Item> search(String name, long idOwner) {
        ArrayList<Item> listItem = new ArrayList<>();
        if (name.equals("")) {
            return listItem;
        }
        for (Item item : items.values()) {
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