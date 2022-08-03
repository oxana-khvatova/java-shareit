package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {
    RequestRepository requestRepository;
    ItemRequestMapper itemRequestMapper;

    UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository,
                          ItemRequestMapper itemRequestMapper, UserService userService) {
        this.requestRepository = requestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.userService = userService;
    }

    public ItemRequestDto save(ItemRequest itemRequest, Long userId) {
        userService.findById(userId);
        itemRequest.setUserRequesterId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest itemRequest1 = requestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest1);
    }

    public List<ItemRequestDto> getAllUserRequest(Long userId) {
        userService.findById(userId);
        List<ItemRequest> list = requestRepository.findByOwnerId(userId).stream()
                .sorted((o1, o2) -> o2.getCreated().compareTo(o1.getCreated())).collect(Collectors.toList());
        ;
        return itemRequestMapper.toItemRequestDto(list);
    }

    public List<ItemRequestDto> findAllRequest(int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sortById);

        Page<ItemRequest> requestsPage = requestRepository.findAll(page);
        return itemRequestMapper.toItemRequestDto(requestsPage.getContent());
    }

    public ItemRequestDto getRequest(Long requestId) {
        ItemRequest itemRequest;
        if (requestRepository.findById(requestId).isPresent()) {
            itemRequest = requestRepository.findById(requestId).get();
            return itemRequestMapper.toItemRequestDto(itemRequest);
        }
        return null;
    }
}
