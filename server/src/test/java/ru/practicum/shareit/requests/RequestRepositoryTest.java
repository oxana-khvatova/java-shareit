package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestRepository repository;

    @Test
    public void testFindByOwnerId() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("test");
        em.persist(user);

        User owner = new User();
        owner.setEmail("testOwner@example.com");
        owner.setName("testOwner");
        em.persist(owner);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Lamp");
        itemRequest.setUserRequesterId(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);

        ItemRequest itemRequestOwner = new ItemRequest();
        itemRequestOwner.setDescription("Table");
        itemRequestOwner.setUserRequesterId(owner.getId());
        itemRequestOwner.setCreated(LocalDateTime.now());
        em.persist(itemRequestOwner);

        List<ItemRequest> requests = repository.findByOwnerId(owner.getId());
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(itemRequestOwner, requests.get(0));
    }
}
