package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

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

        Item item2 = new Item();
        item2.setOwner(owner.getId());
        item2.setRequest(itemRequest.getId());
        item2.setAvailable(false);
        item2.setName("test itemOwner");
        item2.setDescription("test item description owner");
        em.persist(item2);

        Booking b = new Booking();
        b.setItemId(i.getId());
        b.setBookerId(user.getId());
        b.setStart(LocalDateTime.of(2022, 9, 2, 11, 10, 15));
        b.setEnd(LocalDateTime.of(2022, 9, 3, 11, 10, 15));
        b.setStatus(Status.WAITING);
        em.persist(b);

        Booking booking1 = new Booking();
        booking1.setItemId(item2.getId());
        booking1.setBookerId(owner.getId());
        booking1.setStart(LocalDateTime.of(2022, 9, 2, 11, 10, 15));
        booking1.setEnd(LocalDateTime.of(2022, 9, 3, 11, 10, 15));
        booking1.setStatus(Status.WAITING);
        em.persist(booking1);

        List<Booking> bookings = repository.findByOwnerId(i.getOwner());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(b, bookings.get(0));
    }
}
