package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;
    UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> getAll() {
        List<User> allUsers = new ArrayList<>(userService.findAll());
        log.info("Пользователей в базе: {}", allUsers.size());
        return userMapper.toUserDtoList(allUsers);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Запрошен пользователь id: " + id);
        User user = userService.findById(id);
        return userMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = userService.save(userDto);
        log.info("Новый пользователь: " + user);
        return userMapper.toUserDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Valid @RequestBody UserForUpdate userForUpdate) {
        User user = userService.upDate(userForUpdate, id);
        log.info("Update user: " + user);
        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        userService.deleteById(id);
    }
}