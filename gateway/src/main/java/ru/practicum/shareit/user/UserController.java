package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdate;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user) {
        return userClient.create(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @Valid @RequestBody UserForUpdate userForUpdate) {
        return userClient.update(id, userForUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable long id) {
        return userClient.deleteById(id);
    }
}
