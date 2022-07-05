package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;

import java.util.Collection;

public interface UserStorage {

    User add(User user);

    User update(long id, UserForUpdate newUser);

    void delete(User user);

    Collection<User> findAll();

    User findById(Long id);
}