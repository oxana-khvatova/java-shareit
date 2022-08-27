package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserServiceTest {
    static User user;
    static UserForUpdate updatedUser;
    static User badUser;

    @BeforeEach
    public void init() {
        user = new User();
        user.setName("Ivan");
        user.setEmail("email@com.ru");
        user.setId(10L);

        updatedUser = new UserForUpdate();
        updatedUser.setName("Maxim");
        updatedUser.setEmail("email123@com.ru");
        updatedUser.setId(10L);

        badUser = new User();
        badUser.setName("Maxim");
        badUser.setEmail("emailcom.ru");
        badUser.setId(11L);
    }

    @Test
    void shouldCreateUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);

        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        User returnedUser = userService.save(user);
        Assertions.assertEquals(returnedUser, user);
    }

    @Test
    void shouldUpdateUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);

        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));

        User returnedUser = userService.upDate(updatedUser, updatedUser.getId());
        Assertions.assertEquals(returnedUser.getName(), updatedUser.getName());
        Assertions.assertEquals(returnedUser.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(returnedUser.getId(), updatedUser.getId());
    }

    @Test
    void shouldFindById() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);


        Mockito
                .when(mockUserRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockUserRepository.findById(badUser.getId()))
                .thenReturn(Optional.empty());

        User user2 = userService.findById(user.getId());
        Assertions.assertEquals(user2, user);
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.findById(badUser.getId());
        });
    }

    @Test
    void shouldFindAll() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);
        List<User> users = new ArrayList<>();
        users.add(user);
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(users);
        List<User> users2 = userService.findAll();
        Assertions.assertEquals(users, users2);
    }

    @Test
    void shouldDeleteUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);
        userService.deleteById(10L);
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(10L);
    }
}
