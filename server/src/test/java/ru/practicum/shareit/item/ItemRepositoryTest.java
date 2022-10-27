package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

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

        Item i = new Item();
        i.setOwner(user.getId());
        i.setRequest(itemRequest.getId());
        i.setAvailable(true);
        i.setName("test item");
        i.setDescription("test item description");
        em.persist(i);

        Comments comments = new Comments();
        comments.setText("New");
        comments.setAuthorId(user.getId());
        comments.setCreated(LocalDateTime.now());
        comments.setItemId(i.getId());
        em.persist(comments);

        em.refresh(i);

        Item item2 = new Item();
        item2.setOwner(owner.getId());
        item2.setRequest(itemRequest.getId());
        item2.setAvailable(true);
        item2.setName("test itemOwner");
        item2.setDescription("test item description owner");
        em.persist(item2);

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, 3, sortById);

        List<Item> items = repository.search("test", page);
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(i, items.get(0));
    }
}
