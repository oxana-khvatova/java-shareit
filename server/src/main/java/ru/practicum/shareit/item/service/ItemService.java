package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.exception.BookingIsImpossible;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.mapper.ItemMapperForOwner;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.ItemForUpdate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private ItemMapperForOwner itemMapperForOwner;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       CommentRepository commentRepository, BookingRepository bookingRepository, ItemMapper itemMapper,
                       ItemMapperForOwner itemMapperForOwner) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
        this.itemMapperForOwner = itemMapperForOwner;
    }

    public List<Item> search(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        return itemRepository.search(text, page);
    }

    public Item save(Item item, Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            item.setOwner(userId);
        } else {
            throw new UserNotFoundException("User does not exist");
        }
        return itemRepository.save(item);
    }

    public ItemDto upDate(ItemForUpdate item, Long userId, Long itemId) {
        Item itemUpDate = findById(itemId);
        if (!Objects.equals(itemUpDate.getOwner(), userId)) {
            throw new ForbiddenAccessException("Updating an item is only possible for owner");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemUpDate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpDate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpDate.setAvailable(item.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(itemUpDate));
    }

    public Item findById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            return item.get();
        } else throw new ItemNotFoundException("Item id= " + id + " not found");
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public List<Item> findAllOwnerItems(Long idOwner, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        return itemRepository.findByOwner(idOwner, page);
    }

    public Comments addComments(Long userId, Long itemId, Comments comments) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        comments.setCreated(LocalDateTime.now());
        for (Booking book : bookings) {
            if (Objects.equals(book.getBookerId(), userId) && (book.getStatus() == Status.APPROVED ||
                    (book.getStatus() == Status.CANCELED)) && comments.getCreated().isAfter(book.getStart())) {
                Item item = findById(itemId);
                comments.setAuthorId(userId);
                comments.setItemId(itemId);
                item.getComments().add(comments);
                return commentRepository.save(comments);
            }
        }
        throw new BookingIsImpossible(" Not possible");
    }
}