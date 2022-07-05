package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public List<User> getAll() {
        List<User> allUsers = new ArrayList<>(inMemoryUserStorage.findAll());
        log.info("Пользователей в базе: {}", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Запрошен пользователь id: " + id);
        return inMemoryUserStorage.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        inMemoryUserStorage.add(user);
        log.info("Новый пользователь: " + user);
        return user;
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable long id, @Valid @RequestBody UserForUpdate user) {
        log.info("Update user: " + user);
        return inMemoryUserStorage.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        inMemoryUserStorage.delete(inMemoryUserStorage.findById(id));
    }
}