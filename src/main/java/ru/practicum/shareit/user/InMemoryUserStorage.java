package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;

import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private static long idGenerator = 1;

    @Override
    public User add(User user) {
        if (user.getId() != null) {
            log.error("Can't add user: validation failed id != 0");
            throw new ValidationException();
        }
        if (!users.containsKey(user.getId()) && !emails.contains(user.getEmail())) {
            user.setId(idGenerator++);
            emails.add(user.getEmail());
            users.put(user.getId(), user);
            log.info("Add user" + user);
            return user;
        } else {
            log.error("User already exist");
            throw new UserAlreadyExist("User " + user + " not found ");
        }
    }

    @Override
    public User update(long id, UserForUpdate newUser) {
        if (id == 0) {
            log.error("Can't update user: validation failed id == 0");
            throw new ValidationException();
        }
        List<User> userList = new ArrayList<>(users.values());
        User user = users.get(id);
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            for (User u : userList) {
                if (u.getEmail().equals(newUser.getEmail()) && (u.getId() != id)) {
                    log.error("Can't update user: validation failed email not uniq");
                    throw new UserAlreadyExist("Пользователь с таким email ужу существует");
                }
            }
            String oldEmail = user.getEmail();
            user.setEmail(newUser.getEmail());
            emails.remove(oldEmail);
            emails.add(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        users.replace(user.getId(), user);
        log.info("Updated user " + user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void delete(User user) {
        if (user.getId() == 0) {
            log.error("Can't delete user: validation failed id != 0");
            throw new ValidationException();
        }
        if (users.containsKey(user.getId())) {
            emails.remove(user.getEmail());
            users.remove(user.getId());
        } else {
            throw new UserNotFoundException("User " + user + " not found");
        }
    }

    @Override
    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("User with " + id + " not found");
        }
        return users.get(id);
    }
}