package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserStorage inMemoryUserStorage;
    UserMapper userMapper;

    @Autowired
    public UserController(UserStorage inMemoryUserStorage, UserMapper userMapper) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> getAll() {
        List<User> allUsers = new ArrayList<>(inMemoryUserStorage.findAll());
        log.info("Пользователей в базе: {}", allUsers.size());
        return userMapper.toUserDtoList(allUsers);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Запрошен пользователь id: " + id);
        User user = inMemoryUserStorage.findById(id);
        return userMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody User user) {
        inMemoryUserStorage.add(user);
        log.info("Новый пользователь: " + user);
        return userMapper.toUserDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Valid @RequestBody UserForUpdate user) {
        log.info("Update user: " + user);
        User userUpdate = inMemoryUserStorage.update(id, user);
        return userMapper.toUserDto(userUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        inMemoryUserStorage.delete(inMemoryUserStorage.findById(id));
    }
}