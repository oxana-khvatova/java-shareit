package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemMapperForOwner {
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemMapperForOwner(BookingRepository bookingRepository, CommentMapper commentMapper) {
        this.bookingRepository = bookingRepository;
        this.commentMapper = commentMapper;
    }

    public ItemOwnerDto toItemOwnerDto(Item item, Long userId) {
        if (!Objects.equals(item.getOwner(), userId)) {
            return new ItemOwnerDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest() != null ? item.getRequest() : null,
                    item.getOwner() != null ? item.getOwner() : null,
                    commentMapper.toCommentListDto(item.getComments()),
                    null,
                    null
            );
        }
        List<Booking> bookingForItem = bookingRepository.findByItemId(item.getId());
        List<Booking> bookPrev = new ArrayList<>();
        List<Booking> bookNext = new ArrayList<>();
        for (Booking book : bookingForItem) {
            if (book.getEnd().isBefore(LocalDateTime.now())) {
                bookPrev.add(book);
            } else if (book.getStart().isAfter(LocalDateTime.now())) {
                bookNext.add(book);
            }
        }
        ItemOwnerDto.BookingItemDto prev = null;
        ItemOwnerDto.BookingItemDto next = null;
        if (bookPrev.size() != 0) {
            List<Booking> sortBookPrev = bookPrev.stream()
                    .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
            Booking itemOwnerDtoPtev = sortBookPrev.get(sortBookPrev.size() - 1);
            prev = new ItemOwnerDto.BookingItemDto(itemOwnerDtoPtev.getId(), itemOwnerDtoPtev.getBookerId());
        }
        if (bookNext.size() != 0) {
            List<Booking> sortBookNext = bookNext.stream()
                    .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
            Booking itemOwnerDtoNext = sortBookNext.get(0);
            next = new ItemOwnerDto.BookingItemDto(itemOwnerDtoNext.getId(), itemOwnerDtoNext.getBookerId());
        }

        return new ItemOwnerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                item.getOwner() != null ? item.getOwner() : null,
                commentMapper.toCommentListDto(item.getComments()),
                prev,
                next
        );
    }

    public List<ItemOwnerDto> toItemDtoList(List<Item> items, Long userId) {
        List<ItemOwnerDto> listDto = new ArrayList<>();
        if (items.size() == 0) {
            return listDto;
        }
        for (Item item : items) {
            ItemOwnerDto dto = toItemOwnerDto(item, userId);
            listDto.add(dto);
        }
        return listDto;
    }
}